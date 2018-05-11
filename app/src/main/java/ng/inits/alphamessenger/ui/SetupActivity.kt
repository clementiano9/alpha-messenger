package ng.inits.alphamessenger.ui

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySetupBinding = DataBindingUtil.setContentView(this, R.layout.activity_setup)
        val model = SetupViewModel()
        binding.model = model
    }
}
