package com.unicomer.e_tracker_test

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.model.Document
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.models.Travel
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeTravelFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeTravelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeTravelFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    //accediendo a los datos de firebase
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")
    var idTravel: String="" //travelRef.document().toString()
    val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    //Instancia del Adapter para el RecyclerView
    var adapterHt: AdapterHomeTravel? = null

    //Obteniendo referencias del layout
    var originCountry: TextView?=null
    var destinyCountry: TextView?=null
    var initDate: TextView?=null
    var finishDate: TextView?=null
    var balance: TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //Toast.makeText(context,"el usuario es: ${FirebaseUser!!.email}",Toast.LENGTH_LONG).show()
        //fillForm()
        travelRef
            .whereEqualTo("emailUser", FirebaseUser!!.email)
            .whereEqualTo("active", true).addSnapshotListener{ querySnapshot, _ ->
                idTravel = querySnapshot!!.documents[0].id
            }
        Log.i("IDTRAVEL3", "el id es: $idTravel")
        return inflater.inflate(R.layout.fragment_home_travel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originCountry = view.findViewById(R.id.txt_header_originCountry)
        destinyCountry = view.findViewById(R.id.txt_header_originDestiny)
        initDate = view.findViewById(R.id.txt_header_initDate)
        finishDate = view.findViewById(R.id.txt_header_finishDate)
        balance = view.findViewById(R.id.txt_header_cash)
        fillForm()//metodo para llenar la cabecera

        setUpRecyclerView("78Z1i0Uu8lhuruplkVLQ")
        Log.i("IDTRAVEL2", "el id es: $idTravel") //no muestra el id

    }

    override fun onStart() {
        super.onStart()
        adapterHt!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterHt!!.startListening()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    fun fillForm(){ //metodo para llenar la cabecera de info del viaje
        var data: MutableList<Travel>
        travelRef
            .whereEqualTo("emailUser", FirebaseUser!!.email)
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { doc ->
                data = doc.toObjects(Travel::class.java)
                originCountry!!.text = data[0].originCountry
                destinyCountry!!.text = data[0].destinyCountry
                initDate!!.text = data[0].initialDate
                finishDate!!.text = data[0].finishDate
                balance!!.text = data[0].balance
                Log.i("IDTRAVEL", "el id es: $idTravel")//Este si muestra el id
            }

    }
    fun setUpRecyclerView(id:String){ //metodo para llenar el recyclerview desde firebase id=el id del Record a llenar
        var query: Query = travelRef.document(id)
            .collection("record").orderBy("recordName")
        var options: FirestoreRecyclerOptions<Record> = FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
        adapterHt = AdapterHomeTravel(options)
        val recycler = view?.findViewById<RecyclerView>(R.id.recyclerRecord)
        recycler!!.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this.context)
        recycler.adapter = adapterHt
    }
    /*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeTravelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeTravelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
