package me.ahobson.myandroidtimetable.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import me.ahobson.myandroidtimetable.AlexsLoginListener
import me.ahobson.myandroidtimetable.R


class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_sign_in, container, false)

        val mytimetableButton = view.findViewById<Button>(R.id.mytimetable_button)
        mytimetableButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.mytimetable_url))
            startActivity(i)
        }

        val loginButton = view.findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            val activity = requireActivity()
            if (activity is AlexsLoginListener) {
                val urlField: EditText = view.findViewById<EditText>(R.id.urlField)
                val text: String = urlField.text.toString()
                activity.loginWithUrl(text)
            }
        }

        return view
    }
}