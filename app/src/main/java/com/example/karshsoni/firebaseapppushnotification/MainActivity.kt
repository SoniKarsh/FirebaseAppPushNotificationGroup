package com.example.karshsoni.firebaseapppushnotification

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.InstanceIdResult
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    companion object {
        val isActivityRunning = true
    }

    val TAG = "MainActivity"
    val BASE_URL = "https://fcm.googleapis.com/fcm/"
    lateinit var server_key: String
    lateinit var userTokens: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        createServerNode()
        getServerKey()
        retrieveTokens()
//        var intent = Intent(this, MyFirebaseMessagingService::class.java)
//        startService(intent)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity, OnSuccessListener<InstanceIdResult> { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
            sendRegistrationToServer(newToken)
        })

        btnSendNotification.setOnClickListener {

            sendMessageToAll(dataMessage.text.toString(), dataTitle.text.toString(),
                    notiMessage.text.toString(), notiTitle.text.toString())

        }

    }
    fun retrieveTokens(){
        val dataBase = FirebaseDatabase.getInstance()
        val reference = dataBase.reference
        val query: Query = reference.child("User").orderByValue()
        userTokens = ArrayList()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var dataSnapshot: DataSnapshot
                for(i in p0.children){
                    dataSnapshot = i.child("token")
                    userTokens.add(dataSnapshot.value.toString())
                }
                Log.i("user_tokens", userTokens.toString())
            }
        })
    }

    fun sendMessageToAll(message: String, title: String, noti_message: String, noti_title: String) {
        Log.d(TAG, "sendMessageToAll: Sending Message To All")

        var retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        var fcmAPI = retrofit.create(FCM::class.java)

        // attach the headers
        var headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "key=$server_key"

        for(token in userTokens){
            var fcmModelData = Data(title, message)
            var fcmModelNotification = Notification(noti_message, noti_title)
            var firebaseCloudMessage = FCMResponse(fcmModelNotification, fcmModelData, token)

            var call = fcmAPI.send(headers, firebaseCloudMessage)

            call.enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    Log.d(TAG, "onFailure: "+t.toString())
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    Log.d(TAG, "onResponse: "+response.toString())
                }

            })

        }

    }

    fun getServerKey() {
        val dataBase = FirebaseDatabase.getInstance()
        val reference = dataBase.reference
        val query: Query = reference.child("Server").orderByValue()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var dataSnapshot: DataSnapshot = p0.children.iterator().next()
                server_key = dataSnapshot.value.toString()
                Log.i("server_key", server_key)
            }

        })
    }

    fun sendRegistrationToServer(token: String) {
        val dataBaseReg = FirebaseDatabase.getInstance()
        var referenceReg: DatabaseReference = dataBaseReg.reference.child("User").child("Emulator")
        referenceReg.child("token").setValue(token)
    }

    fun createServerNode() {
        val dataBaseCre = FirebaseDatabase.getInstance()
        var referenceCre: DatabaseReference = dataBaseCre.reference.child("Server")
        referenceCre.child("server_key").setValue("Add Your Server Key Here")
    }

}
