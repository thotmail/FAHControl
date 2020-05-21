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
import java.lang.Integer.parseInt
import java.util.regex.Pattern

class AddConnectionActivity: AppCompatActivity() {

    val partialIPAddressPattern: Pattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$")
    private val partOfIP = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
    val ipAddressPattern: Pattern = Pattern.compile("$partOfIP\\.$partOfIP\\.$partOfIP\\.$partOfIP")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connection)
        var passack = false
        val oriColor = NicknamePrompt.currentTextColor

        IPAddrInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            var prev = ""
            override fun afterTextChanged(s: Editable?) {
                if(partialIPAddressPattern.matcher(s).matches()){
                    prev = s.toString()
                }
                else{
                    s?.replace(0, s.length, prev)
                }
                if(ipAddressPattern.matcher(s).matches()){
                    IPAddrWarn.visibility = View.GONE
                }
            }
        })


        AddConnectionSubmit.setOnClickListener {
            var submit = true
            PasswordWarn.visibility = View.GONE
            NicknamePrompt.setTextColor(oriColor)
            NicknameInput.background = null

            IPAddrPrompt.setTextColor(oriColor)
            IPAddrWarn.visibility = View.GONE
            IPAddrInput.background = null

            if (!passack && PasswordInput.text.toString().isNotEmpty()){
                PasswordWarn.visibility = View.VISIBLE
                passack = true
                submit = false
            }
            if(NicknameInput.text.toString().isEmpty()){
                NicknamePrompt.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorError))
                NicknameInput.background = resources.getDrawable(R.drawable.border_error, null)
                submit = false
            }
            if(IPAddrInput.text.toString().isEmpty()){
                IPAddrPrompt.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorError))
                IPAddrInput.background = resources.getDrawable(R.drawable.border_error, null)
                submit = false
            }
            else if(!ipAddressPattern.matcher(IPAddrInput.text.toString()).matches()){
                IPAddrWarn.visibility = View.VISIBLE
                IPAddrPrompt.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorError))
                IPAddrInput.background = resources.getDrawable(R.drawable.border_error, null)
                submit = false
            }

            if(submit){
                val resultIntent = Intent()

                val ip = IPAddrInput.text.toString()
                val nick = NicknameInput.text.toString()

                var port = 36330
                var pass = ""

                if(PortInput.text.toString().isNotEmpty()){
                    port = parseInt(PortInput.text.toString())
                }

                if(PasswordInput.text.toString().isNotEmpty()){
                    pass = PasswordInput.text.toString()
                }

                resultIntent.putExtra("info", ConnectionInfo(ip, port, nick, pass))

                setResult(Activity.RESULT_OK, resultIntent)
                finish()

            }


        }

    }

}