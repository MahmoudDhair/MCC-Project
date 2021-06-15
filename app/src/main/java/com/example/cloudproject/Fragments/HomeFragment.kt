package com.example.cloudproject.Fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudproject.DataAdapter
import com.example.cloudproject.Models.DataModel
import com.example.cloudproject.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.firestore.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
private val collectionReference: CollectionReference = db.collection("data")
var dataAdapter: DataAdapter? = null

lateinit var recyclerView: RecyclerView



/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var videoURL:String = "https://r1---sn-5hnekn7k.googlevideo.com/videoplayback?expire=1623605567&ei=3-zFYKW5Kpqc8gOyi7boCA&ip=178.150.215.68&id=o-AFG6Q_JgVMMJw1CjBeG3O2h5dwqH5hpwiCCMPgvJBO4q&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=6bfvmXnqTjp5QSaFcpFQ6nMF&gir=yes&clen=41751337&ratebypass=yes&dur=702.264&lmt=1582385142897675&fexp=24001373,24007246&c=WEB&txp=5431432&n=GmeYCVU3rZpz4c49KTn&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRgIhAJW9qoQoaDPolAnQKcsgfzGJQclkvAiQMUl37rq0QLqpAiEAvI_jr5R2dt9UebPiOi-ok5kLJKv1FFaAQcfd7OT4k_w%3D&rm=sn-ugpva5o-qo3l7e,sn-4g5eds7z&req_id=97f1a789d5f1a3ee&cm2rm=sn-3c2ek7e&redirect_counter=3&cms_redirect=yes&mh=gX&mip=188.161.143.243&mm=39&mn=sn-5hnekn7k&ms=ltr&mt=1623583468&mv=u&mvi=1&pl=23&lsparams=mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRgIhAJS3B1XvZFUUvYLGncO65BzNgGz3r3_lxnjq-iA3AqXoAiEAnbfZLHBZfhSoptgxnV5tt3su5xxYmwzBUNyZBedNmRY%3D"
    lateinit var playerView: PlayerView
    lateinit var player: SimpleExoPlayer
    private var play_when_ready:Boolean = true
    private var current_window:Int = 0
    private var playpack_position:Long = 0

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

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        playerView = root.findViewById(R.id.videoView)
        setUpRecyleView()


        // Inflate the layout for this fragment
        return root
    }


    public fun initVideo() {
        //player
        player = ExoPlayerFactory.newSimpleInstance(this.context)
        //connect player with player view
        playerView.player = player
        //media source
        var uri: Uri = Uri.parse(videoURL)
        var dataSource: DataSource.Factory = DefaultDataSourceFactory(this.context,"exoplayer-codelab")
        var mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSource).createMediaSource(uri)
        player.playWhenReady = play_when_ready
        player.seekTo(current_window,playpack_position)
        player.prepare(mediaSource,false,false)
    }

    public fun releaseVideo() {
        if (player.isPlaying){
            play_when_ready = player.playWhenReady
            playpack_position = player.currentPosition
            current_window = player.currentWindowIndex
            player.release()
        }
    }


    fun setUpRecyleView() {
        val query: Query = collectionReference




        val firseStoreRecylerOptions: FirestoreRecyclerOptions<DataModel> =
                FirestoreRecyclerOptions.Builder<DataModel>().setQuery(query, DataModel::class.java)
                        .build()
        dataAdapter = DataAdapter(firseStoreRecylerOptions)

        recyclerView.layoutManager = LinearLayoutManager(activity!!)
        recyclerView.adapter = dataAdapter
    }


    override fun onStart() {
        super.onStart()
        dataAdapter!!.startListening()
        initVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        dataAdapter!!.stopListening()
    }

    override fun onResume() {
        super.onResume()
        if(player.isPlaying){
            initVideo()
        }
    }

    override fun onStop() {
        super.onStop()
        releaseVideo()
    }

    override fun onPause() {
        super.onPause()
        releaseVideo()
    }

}