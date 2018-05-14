package ng.inits.alphamessenger.ui.messaging

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_chat_screen.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.databinding.ActivityChatScreenBinding

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var recipientName: String
    private lateinit var viewModel: ChatScreenViewModel
    private lateinit var binding: ActivityChatScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_screen)
        viewModel = ChatScreenViewModel()
        binding.model = viewModel

        getDataFromIntent()
        setSupportActionBar(toolbar)
        supportActionBar?.title = recipientName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getDataFromIntent() {
        recipientName = intent.getStringExtra(ChatContract.CHAT_RECIPIENT_NAME)
        Log.d(TAG, "Recipient name: $recipientName")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG: String = ChatScreenActivity::class.java.simpleName
    }
}
