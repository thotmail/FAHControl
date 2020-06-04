package ca.thotmail.fahcontrol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ca.thotmail.fahcontrol.storage.ConnectionInfo
import kotlinx.android.synthetic.main.activity_add_connection.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Integer.parseInt
import java.util.regex.Pattern

/**
 * used to edit as well.
 */

class AddConnectionActivity: AppCompatActivity() {

    private var oriColor : Int = 0

    val partialIPAddressPattern: Pattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$")
    private val partOfIP = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
    val ipAddressPattern: Pattern = Pattern.compile("$partOfIP\\.$partOfIP\\.$partOfIP\\.$partOfIP")

    private fun resetUI(){
        PasswordWarn.visibility = View.GONE
        NicknamePrompt.setTextColor(oriColor)
        NicknameInput.background = null

        IPAddrPrompt.setTextColor(oriColor)
        IPAddrWarn.visibility = View.GONE
        IPAddrInput.background = null

        DeleteWarn.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connection)
        var passack = false
        oriColor = NicknamePrompt.currentTextColor
        var edit = false

        //if this instance is to be used to edit
        if(intent.hasExtra("toEdit")){
            val info = intent.getSerializableExtra("toEdit") as ConnectionInfo
            var delack = false
            edit = true
            supportActionBar?.title = "Edit ${info.nickname}"
            NicknameInput.setText(info.nickname)
            IPAddrInput.setText(info.ipAddr)
            PortInput.setText("${info.port}")
            PasswordInput.setText(info.paswd)
            AddConnectionSubmit.text = "Done"
            AddConnectionDelete.visibility = View.VISIBLE
            AddConnectionDelete.setOnClickListener {
                resetUI()
                if(delack) {
                    val res = Intent()
                    res.putExtra("toEdit", info)
                    setResult(Activity.RESULT_OK, res)
                    finish()
                }
                else{
                    delack = true
                    DeleteWarn.visibility = View.VISIBLE
                }
            }
        }

        //it takes long to load up the activity... could just be my emulator but I put shit in another thread just in case
        GlobalScope.launch(Dispatchers.Default) {
            IPAddrInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                var prev = ""
                override fun afterTextChanged(s: Editable?) {
                    if (partialIPAddressPattern.matcher(s).matches()) {
                        prev = s.toString()
                    } else {
                        s?.replace(0, s.length, prev)
                    }
                    if (ipAddressPattern.matcher(s).matches()) {
                        IPAddrWarn.visibility = View.GONE
                    }
                }
            })

            AddConnectionSubmit.setOnClickListener {
                var submit = true
                resetUI()

                if (!passack && PasswordInput.text.toString().isNotEmpty()) {
                    PasswordWarn.visibility = View.VISIBLE
                    passack = true
                    submit = false
                }
                if (NicknameInput.text.toString().isEmpty()) {
                    NicknamePrompt.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorError
                        )
                    )
                    NicknameInput.background = resources.getDrawable(R.drawable.border_error, null)
                    submit = false
                }
                if (IPAddrInput.text.toString().isEmpty()) {
                    IPAddrPrompt.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorError
                        )
                    )
                    IPAddrInput.background = resources.getDrawable(R.drawable.border_error, null)
                    submit = false
                } else if (!ipAddressPattern.matcher(IPAddrInput.text.toString()).matches()) {
                    IPAddrWarn.visibility = View.VISIBLE
                    IPAddrPrompt.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorError
                        )
                    )
                    IPAddrInput.background = resources.getDrawable(R.drawable.border_error, null)
                    submit = false
                }

                if (submit) {
                    val resultIntent = Intent()

                    val ip = IPAddrInput.text.toString()
                    val nick = NicknameInput.text.toString()

                    var port = 36330
                    var pass = ""

                    if (PortInput.text.toString().isNotEmpty()) {
                        port = parseInt(PortInput.text.toString())
                    }

                    if (PasswordInput.text.toString().isNotEmpty()) {
                        pass = PasswordInput.text.toString()
                    }

                    resultIntent.putExtra("info", ConnectionInfo(ip, port, nick, pass))

                    if(edit){
                        resultIntent.putExtra("toEdit", intent.getSerializableExtra("toEdit"))
                    }

                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()

                }


            }
        }

    }

}