package ca.thotmail.fahcontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_add_connection.*
import java.util.regex.Pattern

class AddConnectionActivity: AppCompatActivity() {

    val PARTIAL_IP_ADDRESS : Pattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connection)
        var passack = false

        IPAddrInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            var prev = ""
            override fun afterTextChanged(s: Editable?) {
                if(PARTIAL_IP_ADDRESS.matcher(s).matches()){
                    prev = s.toString()
                }
                else{
                    s?.replace(0, s.length, prev)
                }
            }
        })

        AddConnectionSubmit.setOnClickListener {
            if (!passack && PasswordInput.toString().isNotEmpty()){
                PasswordWarn.visibility = View.VISIBLE
                passack = true
            }
            else{

            }
        }

    }

}