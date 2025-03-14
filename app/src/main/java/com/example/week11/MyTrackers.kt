package com.example.week11

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.week11.R
import com.example.week11.databinding.ActivityMyTrackersBinding
import com.google.firebase.database.FirebaseDatabase

private lateinit var  binding: ActivityMyTrackersBinding
class MyTrackers : AppCompatActivity() {
    var adapter:ContactAdapter?=null
    var listOfContact=ArrayList<UserContact>()
    var userData:UserData?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTrackersBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        dummpyData()

        adapter = ContactAdapter(this,listOfContact)

        binding.lvContactList.adapter = adapter
        binding.lvContactList.onItemClickListener= AdapterView.OnItemClickListener{
                parent,view,postion,id ->
            val userInfo =listOfContact[postion]
            UserData.myTrackers.remove(userInfo.phoneNumber)
            refreshData()

            //save to shared ref
            userData?.saveContactInfo()

            // remove to Realtime database
            val mDatabase = FirebaseDatabase.getInstance().reference
            val userData= UserData(applicationContext)
            mDatabase.child("Users").child(userInfo.phoneNumber!!).child("Finders").child(userData.loadPhoneNumber()).removeValue()

        }
        userData?.loadContactInfo()
        refreshData()
    }

    fun dummpyData(){
        listOfContact.add(UserContact("hussein","3434"))
        listOfContact.add(UserContact("jena","344343"))
        listOfContact.add(UserContact("laya","434543"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater=menuInflater
        inflater.inflate(R.menu.tracker_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            R.id.finishActivity ->{
               finish()
            }
            R.id.addContact ->{
                checkPermission()
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    val CONTACT_CODE =123
    fun checkPermission() {
        if (Build.VERSION.SDK_INT>=23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED ) {

                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), CONTACT_CODE)
                return
            }
        }
        pickContact()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            CONTACT_CODE-> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickContact()
                } else {
                    Toast.makeText(this, "Cannot acces to contact ", Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }

        }


    }

    val PCIK_CODE=1234
    fun pickContact() {

        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PCIK_CODE)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            PCIK_CODE->{
                if(resultCode== Activity.RESULT_OK){
                    val contactData = data!!.data
                    val c = contactData?.let { contentResolver.query(it,null,null,null) }
                    if (c!!.moveToFirst()){
                        val id= c!!.getString(c!!.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val hasPhone= c!!.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        if (hasPhone.equals("1")){
                            val phones= contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null,null)

                            phones!!.moveToFirst()
                            var phoneNumber = phones.getString(phones!!.getColumnIndexOrThrow("data1"))
                            val name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                            phoneNumber=UserData.formatPhoneNumber(phoneNumber)
                            UserData.myTrackers.put(phoneNumber,name)
                            refreshData()
                            userData?.saveContactInfo()

                            val mDatabase = FirebaseDatabase.getInstance().reference
                            val userData= UserData(applicationContext)
                            mDatabase.child("Users").child(phoneNumber).child("Finders").child(userData.loadPhoneNumber()).setValue(true)

                        }
                    }
                }
            }
            else ->{
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


    fun refreshData(){
        listOfContact.clear()
        for ((key,value) in UserData.myTrackers){
            listOfContact.add(UserContact(value,key))
        }

        adapter!!.notifyDataSetChanged()
    }
    class ContactAdapter: BaseAdapter {
        var listOfContact=ArrayList<UserContact>()
        var context: Context?=null
        constructor(context: Context, listOfContact:ArrayList<UserContact>){
            this.context=context
            this.listOfContact=listOfContact
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val userContact = listOfContact[p0]
            val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val contactTicketView= inflator.inflate(R.layout.contact_ticket,null)
            contactTicketView.findViewById<TextView>(R.id.tvName).text= userContact.name
            contactTicketView.findViewById<TextView>(R.id.tvPhoneNumber).text= userContact.phoneNumber

            return contactTicketView
        }

        override fun getItem(p0: Int): Any {

            return listOfContact[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listOfContact.size
        }

    }

}