package com.breezedsm.features.dashboard.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breezedsm.CustomConstants
import com.breezedsm.CustomStatic
import com.breezedsm.Customdialog.CustomDialog
import com.breezedsm.Customdialog.OnDialogCustomClickListener
import com.breezedsm.R
import com.breezedsm.ScreenRecService
import com.breezedsm.app.*
import com.breezedsm.app.domain.*
import com.breezedsm.app.types.DashboardType
import com.breezedsm.app.types.FragType
import com.breezedsm.app.uiaction.IntentActionable
import com.breezedsm.app.utils.*
import com.breezedsm.app.widgets.MovableFloatingActionButton
import com.breezedsm.base.BaseResponse
import com.breezedsm.base.presentation.BaseActivity
import com.breezedsm.base.presentation.BaseActivity.Companion.compositeDisposable
import com.breezedsm.base.presentation.BaseFragment
import com.breezedsm.faceRec.DetectorActivity
import com.breezedsm.faceRec.FaceStartActivity
import com.breezedsm.faceRec.tflite.SimilarityClassifier
import com.breezedsm.faceRec.tflite.TFLiteObjectDetectionAPIModel
import com.breezedsm.features.SearchLocation.locationInfoModel
import com.breezedsm.features.addshop.api.AddShopRepositoryProvider
import com.breezedsm.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.breezedsm.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.breezedsm.features.addshop.api.typeList.TypeListRepoProvider
import com.breezedsm.features.addshop.model.*
import com.breezedsm.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.breezedsm.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.breezedsm.features.alarm.api.attendance_report_list_api.AttendanceReportRepoProvider
import com.breezedsm.features.alarm.model.AlarmData
import com.breezedsm.features.alarm.model.AttendanceReportDataModel
import com.breezedsm.features.alarm.presetation.AttendanceReportAdapter
import com.breezedsm.features.averageshop.api.ShopActivityRepositoryProvider
import com.breezedsm.features.averageshop.business.InfoWizard
import com.breezedsm.features.averageshop.model.ShopActivityRequest
import com.breezedsm.features.averageshop.model.ShopActivityResponse
import com.breezedsm.features.averageshop.model.ShopActivityResponseDataList
import com.breezedsm.features.commondialog.presentation.CommonDialog
import com.breezedsm.features.commondialog.presentation.CommonDialogClickListener
import com.breezedsm.features.createOrder.GetOrderHistory
import com.breezedsm.features.createOrder.GetProductRateReq
import com.breezedsm.features.createOrder.GetProductReq
import com.breezedsm.features.createOrder.OrderListFrag
import com.breezedsm.features.dashboard.presentation.api.dashboardApi.DashboardRepoProvider
import com.breezedsm.features.dashboard.presentation.api.dayStartEnd.DayStartEndRepoProvider
import com.breezedsm.features.dashboard.presentation.api.gteroutelistapi.GetRouteListRepoProvider
import com.breezedsm.features.dashboard.presentation.api.submithomeloc.SubmitHomeLocationRepoProvider
import com.breezedsm.features.dashboard.presentation.model.DaystartDayendRequest
import com.breezedsm.features.dashboard.presentation.model.SelectedRouteListResponseModel
import com.breezedsm.features.dashboard.presentation.model.StatusDayStartEnd
import com.breezedsm.features.dashboard.presentation.model.SubmitHomeLocationInputModel
import com.breezedsm.features.location.LocationWizard
import com.breezedsm.features.location.SingleShotLocationProvider
import com.breezedsm.features.location.UserLocationDataEntity
import com.breezedsm.features.location.api.LocationRepoProvider
import com.breezedsm.features.location.model.*
import com.breezedsm.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.breezedsm.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.breezedsm.features.login.api.LoginRepositoryProvider
import com.breezedsm.features.login.api.alarmconfigapi.AlarmConfigRepoProvider
import com.breezedsm.features.login.api.global_config.ConfigFetchRepoProvider
import com.breezedsm.features.login.api.productlistapi.ProductListRepoProvider
import com.breezedsm.features.login.api.user_config.UserConfigRepoProvider
import com.breezedsm.features.login.model.alarmconfigmodel.AlarmConfigResponseModel
import com.breezedsm.features.login.model.globalconfig.ConfigFetchResponseModel
import com.breezedsm.features.login.model.mettingListModel.MeetingListResponseModel
import com.breezedsm.features.login.model.productlistmodel.ProductListResponseModel
import com.breezedsm.features.login.model.userconfig.UserConfigResponseModel
import com.breezedsm.features.login.presentation.LoginActivity
import com.breezedsm.features.member.api.TeamRepoProvider
import com.breezedsm.features.member.model.TeamAreaListResponseModel
import com.breezedsm.features.member.model.TeamListResponseModel
import com.breezedsm.features.member.model.TeamShopListResponseModel
import com.breezedsm.features.member.model.UserPjpResponseModel
import com.breezedsm.features.nearbyshops.api.ShopListRepositoryProvider
import com.breezedsm.features.nearbyshops.model.*
import com.breezedsm.features.photoReg.api.GetUserListPhotoRegProvider
import com.breezedsm.features.photoReg.model.UserFacePicUrlResponse
import com.breezedsm.features.report.presentation.ReportAdapter
import com.breezedsm.features.shopdetail.presentation.api.EditShopRepoProvider
import com.breezedsm.features.timesheet.api.TimeSheetRepoProvider
import com.breezedsm.features.timesheet.model.TimeSheetConfigResponseModel
import com.breezedsm.features.timesheet.model.TimeSheetDropDownResponseModel
import com.breezedsm.widgets.AppCustomTextView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.hbisoft.hbrecorder.Countdown
import com.hbisoft.hbrecorder.HBRecorder
import com.hbisoft.hbrecorder.HBRecorderListener
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_new.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

/**
 * Created by rp : 31-10-2017:16:49
 */
// Revision 1.0 DashboardFragment  Suman App V4.4.6  09-04-2024  mantis id 27357: stage list api call update on refresh
// Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335
// Rev 3.0 Suman 03-06-2024 Suman DashboardFragment mantis 27499

class DashboardFragment : BaseFragment(), View.OnClickListener, HBRecorderListener,
    View.OnTouchListener {

    var dX = 0f
    var dY = 0f
    var lastAction = 0

    //test by saheliAdd
    private lateinit var fab: FloatingActionButton
    private lateinit var mContext: Context
    private lateinit var mRouteActivityDashboardAdapter: RouteActivityDashboardAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var history_ll: LinearLayout
    private lateinit var shop_ll: LinearLayout
    private lateinit var attandance_ll: LinearLayout
    private lateinit var order_ll: LinearLayout
    private lateinit var order_ll_new: LinearLayout
    private var mFragment: DashboardType = DashboardType.Home
    private lateinit var reportList: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var list: ArrayList<AddShopDBModelEntity>
    private lateinit var avgTime: AppCustomTextView
    private lateinit var avgShop: AppCustomTextView
    private lateinit var avgOrder: AppCustomTextView
    private lateinit var avgOrderNew: AppCustomTextView
    private lateinit var shops_RL: RelativeLayout
    private lateinit var time_RL: RelativeLayout
    private lateinit var price_RL: RelativeLayout
    private lateinit var best_performing_shop_TV: AppCustomTextView
    private lateinit var no_shop_tv: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_view_all: AppCustomTextView
    private lateinit var rv_pjp_list: RecyclerView
    private lateinit var rl_dashboard_fragment_main: RelativeLayout
    private lateinit var tv_shop: TextView
    private lateinit var tv_order: TextView
    private lateinit var no_of_order_TV: AppCustomTextView
    private lateinit var iv_order_icon: ImageView
    private lateinit var iv_quot_icon: ImageView
    private lateinit var fab_bot: MovableFloatingActionButton

    private lateinit var ll_attendance_report_main: LinearLayout
    private lateinit var rv_attendance_report_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var tv_pick_date_range: AppCustomTextView
    private lateinit var progress_wheel_attendance: ProgressWheel

    //private lateinit var shop_tome_order_tab_LL: LinearLayout
    private lateinit var shop_tome_order_tab_LL: HorizontalScrollView

    lateinit var cancel_timer: TextView
    lateinit var pause_record: TextView

    private lateinit var n_shops_TV: AppCustomTextView
    private lateinit var no_of_shop_TV: AppCustomTextView

    private lateinit var n_time_TV: AppCustomTextView
    private lateinit var no_of_time_TV: AppCustomTextView


    private lateinit var start_TV: AppCustomTextView
    private lateinit var end_TV: AppCustomTextView
    private var isCameraDayStart: Boolean = false

    private var reasonDialog: ReasonDialog? = null
    private var reason = ""

    private lateinit var DDVisit_TV: AppCustomTextView
    private lateinit var DDVisitdate_TV: AppCustomTextView


    //private lateinit var StartRL: RelativeLayout
    //private lateinit var endRL: RelativeLayout

    private lateinit var StartRL: FrameLayout
    private lateinit var endRL: FrameLayout


    private lateinit var start_shop: AppCustomTextView
    private lateinit var enddate_TV: AppCustomTextView

    //private var isStartCall: Boolean = false
    //private var isCalledFromStart: Boolean = false
   //private var isEndCall: Boolean = false

    /*horizontal scroll 01-10-2021*/
    private lateinit var ll_dash_total_visit_newD: LinearLayout
    private lateinit var ll_dash_visit_duration_newD: LinearLayout
    private lateinit var ll_dash_total_order_newD: LinearLayout


    private lateinit var ll_dash_day_start_newD: LinearLayout
    private lateinit var ll_dash_point_visit_newD: LinearLayout
    private lateinit var ll_dash_day_end_newD: LinearLayout
    private lateinit var ll_dash_total_mew_order_newD: LinearLayout
    private lateinit var ll_dash_visit_attendance_newD: LinearLayout

    private lateinit var revisit_ll: LinearLayout
    private var lastLat = 0.0
    private var lastLng = 0.0
    private var prevRevisitTimeStamp = 0L
    private var shop_id = ""

    lateinit var simpleDialogProcess : Dialog
    lateinit var dialogHeaderProcess: AppCustomTextView
    lateinit var dialog_yes_no_headerTVProcess: AppCustomTextView


    private val customProgressDialog: CustomProgressDialog by lazy {
        CustomProgressDialog.getInstance()
    }

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    fun getInstance(objects: Any): DashboardFragment {
        val cardFragment = DashboardFragment()
        mFragment = objects as DashboardType
        return cardFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_dashboard_new, container, false)
        //saheli added 27-07-21
        AppUtils.changeLanguage(mContext, "en")



        initView(view)


        if (Pref.isAttendanceFeatureOnly) {

            /*Handler().postDelayed(Runnable {
                getAttendanceReport(AppUtils.getCurrentDateForShopActi())
            }, 500)*/
        } else
            getShopActivityList()

        return view
    }

    private fun getShopActivityList() {
        if (AppDatabase.getDBInstance()!!.shopActivityDao().getAll().isEmpty()) {
            Handler().postDelayed(Runnable {
                Timber.d("DashFrag callShopActivityApi started ${AppUtils.getCurrentDateTime()}")
                callShopActivityApi()
            }, 100)
        } else
            checkToCallMemberList()
    }

    private fun callShopActivityApi() {
        var shopActivity = ShopActivityRequest()
        shopActivity.user_id = Pref.user_id
        shopActivity.session_token = Pref.session_token
        shopActivity.date_span = "30"
        shopActivity.from_date = ""
        shopActivity.to_date = ""
        val repository = ShopActivityRepositoryProvider.provideShopActivityRepository()

        BaseActivity.compositeDisposable.add(
            //repository.fetchShopActivity(shopActivity)
            repository.fetchShopActivitynew(Pref.session_token!!, Pref.user_id!!, "30", "", "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    var shopActityResponse = result as ShopActivityResponse
                    Timber.d("DashFrag callShopActivityApi ${shopActityResponse.status} ${AppUtils.getCurrentDateTime()}")
                    if (shopActityResponse.status == "200") {



                        updateShopTableInDB(shopActityResponse.date_list)


                                var list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                var totalMinute = InfoWizard.getTotalShopVisitTimeForActi()
                                Pref.totalShopVisited = (Pref.totalShopVisited.toInt() - list.size).toString()
                                Pref.totalTimeSpenAtShop = (Pref.totalTimeSpenAtShop.toInt() - totalMinute).toString()

                                val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
                                Timber.e("=======RESPONSE FROM SHOP ACTIVITY API (DASHBOARD FRAGMENT)=======")
                                Timber.e("Today's Shop Visit Count====> $todaysShopVisitCount")

                                avgShop.text = todaysShopVisitCount
                                avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

                                when {
                                    Pref.willActivityShow -> avgOrder.text =
                                        InfoWizard.getActivityForToday()
                                    Pref.isQuotationShow -> avgOrder.text =
                                        getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
                                    else -> avgOrder.text =
                                        getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
                                }



//                                (mContext as DashboardActivity).showSnackMessage(result.message!!)
                    } else {
//                                (mContext as DashboardActivity).showSnackMessage(result.message!!)
                        //TODO SNACK MESSAGE

                    }

                    initBottomAdapter()
                    checkToCallMemberList()
                    (mContext as DashboardActivity).takeActionOnGeofence()
                }, { error ->
                    Timber.d("DashFrag callShopActivityApi error ${error.message} ${AppUtils.getCurrentDateTime()}")
                    println("load_frag callShopActivityApi error")
                    error.printStackTrace()
                    //(mContext as DashboardActivity).showSnackMessage("ERROR")
                    //TODO SNACK MESSAGE
                    initBottomAdapter()
                    checkToCallMemberList()
                    (mContext as DashboardActivity).takeActionOnGeofence()
                })
        )
    }

    private fun updateShopTableInDB(date_list: List<ShopActivityResponseDataList>?) {

        doAsync {

            for (i in date_list!!.indices) {
                for (j in 0 until date_list[i].shop_list!!.size) {
                    var shopActivityEntity = ShopActivityEntity()
                    shopActivityEntity.shopid = date_list[i].shop_list!![j].shopid
                    shopActivityEntity.shop_name = date_list[i].shop_list!![j].shop_name
                    shopActivityEntity.shop_address = date_list[i].shop_list!![j].shop_address
                    shopActivityEntity.date = date_list[i].shop_list!![j].date
                    if (date_list[i].shop_list!![j].duration_spent!!.contains("."))
                        shopActivityEntity.duration_spent =
                            date_list[i].shop_list!![j].duration_spent!!.split(".")[0]
                    else
                        shopActivityEntity.duration_spent = date_list[i].shop_list!![j].duration_spent!!
                    shopActivityEntity.totalMinute =
                        AppUtils.convertMinuteFromHHMMSS(shopActivityEntity.duration_spent)

                    if (!TextUtils.isEmpty(date_list[i].shop_list!![j].start_timestamp))
                        shopActivityEntity.startTimeStamp =
                            date_list[i].shop_list!![j].start_timestamp!!
                    else
                        shopActivityEntity.startTimeStamp = "0"

                    shopActivityEntity.endTimeStamp = "0"
                    shopActivityEntity.visited_date = date_list[i].shop_list!![j].visited_date
                    shopActivityEntity.isUploaded = true
                    shopActivityEntity.isVisited = true
                    shopActivityEntity.isDurationCalculated = true
                    shopActivityEntity.isFirstShopVisited = false
                    shopActivityEntity.distance_from_home_loc = ""

                    shopActivityEntity.device_model = date_list[i].shop_list!![j].device_model
                    shopActivityEntity.android_version = date_list[i].shop_list!![j].android_version
                    shopActivityEntity.battery = date_list[i].shop_list!![j].battery
                    shopActivityEntity.net_status = date_list[i].shop_list!![j].net_status
                    shopActivityEntity.net_type = date_list[i].shop_list!![j].net_type

                    shopActivityEntity.in_time = date_list[i].shop_list!![j].in_time
                    shopActivityEntity.out_time = date_list[i].shop_list!![j].out_time

                    shopActivityEntity.in_loc = date_list[i].shop_list!![j].in_location
                    shopActivityEntity.out_loc = date_list[i].shop_list!![j].out_location
                    shopActivityEntity.shop_revisit_uniqKey = date_list[i].shop_list!![j].Key!!
                    AppDatabase.getDBInstance()!!.shopActivityDao().insertAll(shopActivityEntity)
                }
            }

            uiThread {
                Timber.d("DashFrag callShopActivityApi updateShopTableInDB finished ${AppUtils.getCurrentDateTime()}")
            }
        }



    }

    override fun onResume() {
        super.onResume()

        try {
            Timber.d("lifecycle_activity_tag DashboardFragment onresume()")
            var ordAmt = AppDatabase.getDBInstance()!!.newOrderDataDao().getOrderSumByDate(AppUtils.getCurrentDateForShopActi()).toBigDecimal().toString()
            avgOrderNew.text = mContext.getString(R.string.rupee_symbol) + String.format("%.02f",ordAmt.toDouble())
        }catch(e:Exception){
            e.printStackTrace()
            Timber.d("lifecycle_activity_tag DashboardFragment ex ${e.printStackTrace()}")
        }



        timerTV = view!!.findViewById(R.id.tv_dash_frag_timer)
        if(Pref.IsShowMarketSpendTimer){
            timerTV.visibility = View.VISIBLE
            try{
                if(!Pref.DayStartTime.equals("")){
                    var currentTime = System.currentTimeMillis().toString()
                    var timeDiff = AppUtils.getTimeFromTimeSpan(Pref.DayStartTime,System.currentTimeMillis().toString())
                    timeDiff = timeDiff.split(":").get(0)+":"+timeDiff.split(":").get(1)
                    timerTV.text = "$timeDiff"
                }
            }catch (ex:Exception){
                Timber.d("ex timer text")
            }
        }else{
            timerTV.visibility = View.GONE
        }


        if (hbRecorder != null) {
            if (hbRecorder!!.isBusyRecording) {
                iv_screen_status.setImageResource(R.drawable.green_round)
                pause_record.visibility = View.VISIBLE
                if (hbRecorder!!.isRecordingPaused) {
                    pause_record.text = ">"
                } else {
                    pause_record.text = "||"
                }
            } else {
                pause_record.visibility = View.GONE
                iv_screen_status.setImageResource(R.drawable.red_circle)
            }
        } else {
            pause_record.visibility = View.GONE
            iv_screen_status.setImageResource(R.drawable.red_circle)
        }



        (mContext as DashboardActivity).teamHierarchy.clear()

        val f = mContext.getDatabasePath(AppConstant.DBNAME)
        val dbSize = f.length()
        val dbSizeInKB = dbSize / 1024
        Timber.e("Original DataBase Size====> $dbSize")
        Timber.e("DataBase Size====> $dbSizeInKB KB")

        //checkToCallMemberList()
        writeDataToFile()
    }


    private fun writeDataToFile() {

        val list = AppDatabase.getDBInstance()!!.userLocationDataDao()
            .getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

        val company = JSONArray()

        for (i in 0 until list.size) {
            if (list[i].latitude == null || list[i].longitude == null)
                continue
            val jsonObject = JSONObject()
            jsonObject.put("locationId", list[i].locationId)
            jsonObject.put("latitude", list[i].latitude)
            jsonObject.put("longitude", list[i].longitude)
            jsonObject.put("distance_covered", list[i].distance)
            jsonObject.put("location_name", list[i].locationName)
            jsonObject.put("timestamp", list[i].timestamp)
            jsonObject.put("time", list[i].time)
            jsonObject.put("hours", list[i].hour)
            jsonObject.put("minutes", list[i].minutes)
            jsonObject.put("shops", list[i].shops)
            jsonObject.put("meridiem", list[i].meridiem)
            jsonObject.put("isUploaded", list[i].isUploaded)
            jsonObject.put("updateDate", list[i].updateDate)
            jsonObject.put("updateDateTime", list[i].updateDateTime)
            company.put(jsonObject)
        }

        val accurateObject = JSONObject()
        accurateObject.put("accurate_locations", company)

        /*val parentObject = JSONObject()
        parentObject.put("accurate_loc", accurateObject)*/

        try {
            var output: Writer? = null
            val folderPath = FTStorageUtils.getFolderPath(mContext)
            val file = File("$folderPath/FTS_Todays_Accurate_Location.txt")
            if (file.exists()) {
                Log.e("Location List", "File deleted")
                file.delete()
            }
            output = BufferedWriter(FileWriter(file))
            output.write(accurateObject.toString())
            output.close()
            Log.e("Location list", "Value saved")

            val length = file.length()
            val lengthInKB = length / 1024

            Timber.e("Original today accurate table Size====> $length")
            Timber.e("Today accurate table Size====> $lengthInKB KB")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun updateUI(any: Any) {
        super.updateUI(any)

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
        Timber.e("=======UPDATE UI (DASHBOARD FRAGMENT)=======")
        Timber.e("Today's Shop Visit Count====> $todaysShopVisitCount")

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        when {
            Pref.willActivityShow -> avgOrder.text = InfoWizard.getActivityForToday()
            Pref.isQuotationShow -> avgOrder.text =
                getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
            else -> avgOrder.text =
                getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
        }

        UpdateLocationData()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        println("lifecycle_activity_tag DashboardFragment onAttach()")
    }

    @SuppressLint("RestrictedApi")
    private fun initView(view: View?) {
        timerTV = view!!.findViewById(R.id.tv_dash_frag_timer)
        if(Pref.IsShowMarketSpendTimer){
            timerTV.visibility = View.VISIBLE
        }else{
            timerTV.visibility = View.GONE
        }

        cancel_timer = view!!.findViewById(R.id.cancel_timer)
        iv_screen_status = view!!.findViewById(R.id.iv_screen_status)
        tv_timer = view!!.findViewById(R.id.tv_timer)
        pause_record = view!!.findViewById(R.id.pause_record)
        ll_recorder_root = view!!.findViewById(R.id.ll_recorder_root)
        ll_recorder_root.setOnTouchListener(this);

        cancel_timer.setOnClickListener(this)
        iv_screen_status.setOnClickListener(this)
        pause_record.setOnClickListener(this)

        if (isRecordRootVisible) {
            ll_recorder_root.visibility = View.VISIBLE
        } else {
            ll_recorder_root.visibility = View.GONE
        }


        fab = view!!.findViewById(R.id.fab)
        fab.setOnClickListener(this)
        history_ll = view.findViewById(R.id.history_ll)
        shop_ll = view.findViewById(R.id.shop_ll)
        attandance_ll = view.findViewById(R.id.attandance_ll)
        order_ll = view.findViewById(R.id.order_ll)
        reportList = view.findViewById(R.id.report_RCV)
        avgTime = view.findViewById(R.id.n_time_TV)
        avgShop = view.findViewById(R.id.n_shops_TV)
        avgOrder = view.findViewById(R.id.n_order_TV)
        avgOrderNew = view.findViewById(R.id.n_new_order_TV)
        avgOrder.text = getString(R.string.rupee_symbol) + "0.00"
        avgOrderNew.text = mContext.getString(R.string.rupee_symbol) + "0.00"
        //shops_RL = view.findViewById(R.id.shops_RL)
        //time_RL = view.findViewById(R.id.time_RL)
        //price_RL = view.findViewById(R.id.price_RL)
        best_performing_shop_TV = view.findViewById(R.id.best_performing_shop_TV)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        tv_view_all = view.findViewById(R.id.tv_view_all)
        rl_dashboard_fragment_main = view.findViewById(R.id.rl_dashboard_fragment_main)
        ll_attendance_report_main = view.findViewById(R.id.ll_attendance_report_main)

        rv_pjp_list = view.findViewById(R.id.rv_pjp_list)
        rv_pjp_list.layoutManager = LinearLayoutManager(mContext)

        rv_attendance_report_list = view.findViewById(R.id.rv_attendance_report_list)
        rv_attendance_report_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_pick_date_range = view.findViewById(R.id.tv_pick_date_range)
        progress_wheel_attendance = view.findViewById(R.id.progress_wheel_attendance)
        progress_wheel_attendance.stopSpinning()
        tv_shop = view.findViewById(R.id.tv_shop)
        tv_order = view.findViewById(R.id.tv_order)
        no_of_order_TV = view.findViewById(R.id.no_of_order_TV)
        iv_order_icon = view.findViewById(R.id.iv_order_icon)
        iv_quot_icon = view.findViewById(R.id.iv_quot_icon)
        fab_bot = view.findViewById(R.id.fab_bot)

        shop_tome_order_tab_LL = view.findViewById(R.id.shop_tome_order_tab_LL)

        n_shops_TV = view.findViewById(R.id.n_shops_TV)
        no_of_shop_TV = view.findViewById(R.id.no_of_shop_TV)

        n_time_TV = view.findViewById(R.id.n_time_TV)
        no_of_time_TV = view.findViewById(R.id.no_of_time_TV)


        start_TV = view.findViewById(R.id.start_TV)
        end_TV = view.findViewById(R.id.end_TV)

        DDVisit_TV = view.findViewById(R.id.DDVisit_TV)
        DDVisitdate_TV = view.findViewById(R.id.DDVisitdate_TV)

        StartRL = view.findViewById(R.id.StartRL)
        endRL = view.findViewById(R.id.endRL)

        start_shop = view.findViewById(R.id.start_shop)
        enddate_TV = view.findViewById(R.id.enddate_TV)

        /*horizontal scroll 01-10-2021*/
        ll_dash_total_visit_newD = view.findViewById(R.id.ll_dash_total_visit_newD)
        ll_dash_visit_duration_newD = view.findViewById(R.id.ll_dash_visit_duration_newD)
        ll_dash_total_order_newD = view.findViewById(R.id.ll_dash_total_order_newD)
        ll_dash_day_start_newD = view.findViewById(R.id.ll_dash_day_start_newD)
        ll_dash_point_visit_newD = view.findViewById(R.id.ll_dash_point_visit_newD)
        ll_dash_day_end_newD = view.findViewById(R.id.ll_dash_day_end_newD)
        ll_dash_total_mew_order_newD = view.findViewById(R.id.ll_dash_total_mew_order_newD)
        order_ll_new = view.findViewById(R.id.order_ll_new)
        ll_dash_visit_attendance_newD = view.findViewById(R.id.ll_dash_visit_attendance_newD)

        simpleDialogProcess = Dialog(mContext)
        simpleDialogProcess.setCancelable(false)
        simpleDialogProcess.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialogProcess.setContentView(R.layout.dialog_message_progress)
        dialogHeaderProcess = simpleDialogProcess.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        dialog_yes_no_headerTVProcess = simpleDialogProcess.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView

        revisit_ll = view.findViewById(R.id.revisit_ll)
        revisit_ll.setOnClickListener(this)

        ll_dash_visit_attendance_newD.setOnClickListener(this)

        start_TV.setOnClickListener({ view ->

            var andrV = Build.VERSION.SDK_INT.toInt()
            if (andrV < 26) {
                Timber.d("start_TV.setOnClickListener  andrV < 26"  + " Time :" + AppUtils.getCurrentDateTime())
                val simpleDialogV = Dialog(mContext)
                simpleDialogV.setCancelable(false)
                simpleDialogV.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialogV.setContentView(R.layout.dialog_message)
                val dialogHeaderV =
                    simpleDialogV.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTVV =
                    simpleDialogV.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                if (Pref.user_name != null) {
                    dialog_yes_no_headerTVV.text = "Hi " + Pref.user_name!! + "!"
                } else {
                    dialog_yes_no_headerTVV.text = "Hi User" + "!"
                }
                dialogHeaderV.text = "Android Version is below 8.\n" +
                        "Functionality may not be working properly."

                val dialogYesV = simpleDialogV.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYesV.setOnClickListener({ view ->
                    simpleDialogV.cancel()
                    CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                    CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                    CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                    val stat = StatFs(Environment.getExternalStorageDirectory().path)
                    val bytesAvailable: Long
                    bytesAvailable =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            stat.blockSizeLong * stat.availableBlocksLong
                        } else {
                            stat.blockSize.toLong() * stat.availableBlocks.toLong()
                        }
                    val megAvailable = bytesAvailable / (1024 * 1024)
                    println("storage " + megAvailable.toString());
                    Timber.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())
                    if (megAvailable < 1024) {
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!
                            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader =
                            simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV =
                            simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        if (Pref.user_name != null) {
                            dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                        } else {
                            dialog_yes_no_headerTV.text = "Hi User" + "!"
                        }
                        //dialogHeader.text = "Please make sure that you have Min: 1GB. Upto 5GB(Best performance) memory available to get best login experience."
                        dialogHeader.text =
                            "Please note that memory available is less than 1 GB. App may not function properly. Please make available memory greater than 2 GB for better performance."
                        val dialogYes =
                            simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()

                            if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                                if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                    CustomDialog.getInstance(
                                        AppUtils.hiFirstNameText(),
                                        getString(R.string.battery_setting_message),
                                        "OK",
                                        "",
                                        "0",
                                        object : OnDialogCustomClickListener {
                                            override fun onOkClick() {

                                                startTvClick()

                                            }

                                            override fun onYesClick() {

                                            }

                                            override fun onNoClick() {
                                            }
                                        }).show(
                                        (mContext as DashboardActivity).supportFragmentManager,
                                        "CustomDialog"
                                    )
                                } else {
                                    startTvClick()
                                }
                            } else {
                                startTvClick()
                                /* start_TV.setOnClickListener({ view ->
                                //faceDetectorSetUp()
                                if (!AppUtils.isOnline(mContext)) {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

                                }else if(isStartCall==false && Pref.DayStartMarked == false && Pref.isAddAttendence){

                                    val simpleDialog = Dialog(mContext)
                                    simpleDialog.setCancelable(false)
                                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    simpleDialog.setContentView(R.layout.dialog_yes_no)
                                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                                    //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                    dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                    dialogHeader.text = "Are you sure ?"
                                    val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                                    val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                                    dialogYes.setOnClickListener({ view ->
                                        simpleDialog.cancel()
                                        //if(Pref.IsshowDayStartSelfie){
                                        //isCameraDayStart=true
                                        //initPermissionCheck()
                                        //}else{
                                        progress_wheel.spin()
                                        isCalledFromStart=true
                                        getPicUrl()
                                        //}
                                    })
                                    dialogNo.setOnClickListener({ view ->
                                        simpleDialog.cancel()
                                    })
                                    simpleDialog.show()
                                }
                                else{
                                    println("reg_face - start_tv"+AppUtils.getCurrentDateTime());
                                    if (!AppUtils.isOnline(mContext)) {
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                                    }
                                    else {
                                        if (!Pref.isAddAttendence) {
                                            // 27-08-21 For ITC
                                            val simpleDialog = Dialog(mContext)
                                            simpleDialog.setCancelable(false)
                                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                            simpleDialog.setContentView(R.layout.dialog_message)
                                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                            dialogHeader.text = "Please mark your attendance."
                                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                            dialogYes.setOnClickListener({ view ->
                                                simpleDialog.cancel()
                                            })
                                            simpleDialog.show()
                    //                    (mContext as DashboardActivity).showSnackMessage("Please mark your attendance")
                                        } else {
                                            if (!Pref.DayStartMarked) {

                                                getLocforStart()

                                                *//*val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Are you sure ?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                getLocforStart()
                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()*//*

                        } else {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                            dialogHeader.text = "Your Day started already."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Day started already")
                        }
                    }
                }
            }
        })
*/
                            }

                        })
                        simpleDialog.show()
                    } else {
                        if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                            if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                CustomDialog.getInstance(
                                    AppUtils.hiFirstNameText(),
                                    getString(R.string.battery_setting_message),
                                    "OK",
                                    "",
                                    "0",
                                    object : OnDialogCustomClickListener {
                                        override fun onOkClick() {

                                            startTvClick()

                                        }

                                        override fun onYesClick() {

                                        }

                                        override fun onNoClick() {
                                        }
                                    }).show(
                                    (mContext as DashboardActivity).supportFragmentManager,
                                    "CustomDialog"
                                )
                            } else {
                                startTvClick()
                            }
                        } else {
                            startTvClick()
                            /* start_TV.setOnClickListener({ view ->
                            //faceDetectorSetUp()
                            if (!AppUtils.isOnline(mContext)) {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

                            }else if(isStartCall==false && Pref.DayStartMarked == false && Pref.isAddAttendence){

                                val simpleDialog = Dialog(mContext)
                                simpleDialog.setCancelable(false)
                                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                simpleDialog.setContentView(R.layout.dialog_yes_no)
                                val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                                val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                                //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                dialogHeader.text = "Are you sure ?"
                                val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                                val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                                dialogYes.setOnClickListener({ view ->
                                    simpleDialog.cancel()
                                    //if(Pref.IsshowDayStartSelfie){
                                    //isCameraDayStart=true
                                    //initPermissionCheck()
                                    //}else{
                                    progress_wheel.spin()
                                    isCalledFromStart=true
                                    getPicUrl()
                                    //}
                                })
                                dialogNo.setOnClickListener({ view ->
                                    simpleDialog.cancel()
                                })
                                simpleDialog.show()
                            }
                            else{
                                println("reg_face - start_tv"+AppUtils.getCurrentDateTime());
                                if (!AppUtils.isOnline(mContext)) {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                                }
                                else {
                                    if (!Pref.isAddAttendence) {
                                        // 27-08-21 For ITC
                                        val simpleDialog = Dialog(mContext)
                                        simpleDialog.setCancelable(false)
                                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                        simpleDialog.setContentView(R.layout.dialog_message)
                                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                        //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                        dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                        dialogHeader.text = "Please mark your attendance."
                                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                        dialogYes.setOnClickListener({ view ->
                                            simpleDialog.cancel()
                                        })
                                        simpleDialog.show()
                //                    (mContext as DashboardActivity).showSnackMessage("Please mark your attendance")
                                    } else {
                                        if (!Pref.DayStartMarked) {

                                            getLocforStart()

                                            *//*val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Are you sure ?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                getLocforStart()
                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()*//*

                        } else {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                            dialogHeader.text = "Your Day started already."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Day started already")
                        }
                    }
                }
            }
        })
*/
                        }
                    }
                })
                simpleDialogV.show()
            }
            else {
                Timber.d("start_TV.setOnClickListener  andrV > 26"  + " Time :" + AppUtils.getCurrentDateTime())
                CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                val stat = StatFs(Environment.getExternalStorageDirectory().path)
                val bytesAvailable: Long
                bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    stat.blockSizeLong * stat.availableBlocksLong
                } else {
                    stat.blockSize.toLong() * stat.availableBlocks.toLong()
                }
                val megAvailable = bytesAvailable / (1024 * 1024)
                println("storage " + megAvailable.toString());
                Timber.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())
                if (megAvailable < 1024) {
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    if (Pref.user_name != null) {
                        dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                    } else {
                        dialog_yes_no_headerTV.text = "Hi User" + "!"
                    }
                    //dialogHeader.text = "Please make sure that you have Min: 1GB. Upto 5GB(Best performance) memory available to get best login experience."
                    dialogHeader.text =
                        "Please note that memory available is less than 1 GB. App may not function properly. Please make available memory greater than 2 GB for better performance."
                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()

                        if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                            if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                CustomDialog.getInstance(
                                    AppUtils.hiFirstNameText(),
                                    getString(R.string.battery_setting_message),
                                    "OK",
                                    "",
                                    "0",
                                    object : OnDialogCustomClickListener {
                                        override fun onOkClick() {

                                            startTvClick()

                                        }

                                        override fun onYesClick() {

                                        }

                                        override fun onNoClick() {
                                        }
                                    }).show(
                                    (mContext as DashboardActivity).supportFragmentManager,
                                    "CustomDialog"
                                )
                            } else {
                                startTvClick()
                            }
                        } else {
                            startTvClick()
                            /* start_TV.setOnClickListener({ view ->
                            //faceDetectorSetUp()
                            if (!AppUtils.isOnline(mContext)) {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

                            }else if(isStartCall==false && Pref.DayStartMarked == false && Pref.isAddAttendence){

                                val simpleDialog = Dialog(mContext)
                                simpleDialog.setCancelable(false)
                                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                simpleDialog.setContentView(R.layout.dialog_yes_no)
                                val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                                val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                                //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                dialogHeader.text = "Are you sure ?"
                                val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                                val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                                dialogYes.setOnClickListener({ view ->
                                    simpleDialog.cancel()
                                    //if(Pref.IsshowDayStartSelfie){
                                    //isCameraDayStart=true
                                    //initPermissionCheck()
                                    //}else{
                                    progress_wheel.spin()
                                    isCalledFromStart=true
                                    getPicUrl()
                                    //}
                                })
                                dialogNo.setOnClickListener({ view ->
                                    simpleDialog.cancel()
                                })
                                simpleDialog.show()
                            }
                            else{
                                println("reg_face - start_tv"+AppUtils.getCurrentDateTime());
                                if (!AppUtils.isOnline(mContext)) {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                                }
                                else {
                                    if (!Pref.isAddAttendence) {
                                        // 27-08-21 For ITC
                                        val simpleDialog = Dialog(mContext)
                                        simpleDialog.setCancelable(false)
                                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                        simpleDialog.setContentView(R.layout.dialog_message)
                                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                        //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                        dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                        dialogHeader.text = "Please mark your attendance."
                                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                        dialogYes.setOnClickListener({ view ->
                                            simpleDialog.cancel()
                                        })
                                        simpleDialog.show()
                //                    (mContext as DashboardActivity).showSnackMessage("Please mark your attendance")
                                    } else {
                                        if (!Pref.DayStartMarked) {

                                            getLocforStart()

                                            *//*val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Are you sure ?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                getLocforStart()
                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()*//*

                        } else {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                            dialogHeader.text = "Your Day started already."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Day started already")
                        }
                    }
                }
            }
        })
*/
                        }

                    })
                    simpleDialog.show()
                }
                else {
                    if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                        if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                            CustomDialog.getInstance(
                                AppUtils.hiFirstNameText(),
                                getString(R.string.battery_setting_message),
                                "OK",
                                "",
                                "0",
                                object : OnDialogCustomClickListener {
                                    override fun onOkClick() {

                                        startTvClick()

                                    }

                                    override fun onYesClick() {

                                    }

                                    override fun onNoClick() {
                                    }
                                }).show(
                                (mContext as DashboardActivity).supportFragmentManager,
                                "CustomDialog"
                            )
                        } else {
                            startTvClick()
                        }
                    } else {
                        startTvClick()
                        /* start_TV.setOnClickListener({ view ->
                        //faceDetectorSetUp()
                        if (!AppUtils.isOnline(mContext)) {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

                        }else if(isStartCall==false && Pref.DayStartMarked == false && Pref.isAddAttendence){

                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                            dialogHeader.text = "Are you sure ?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                //if(Pref.IsshowDayStartSelfie){
                                //isCameraDayStart=true
                                //initPermissionCheck()
                                //}else{
                                progress_wheel.spin()
                                isCalledFromStart=true
                                getPicUrl()
                                //}
                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
                        }
                        else{
                            println("reg_face - start_tv"+AppUtils.getCurrentDateTime());
                            if (!AppUtils.isOnline(mContext)) {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                            }
                            else {
                                if (!Pref.isAddAttendence) {
                                    // 27-08-21 For ITC
                                    val simpleDialog = Dialog(mContext)
                                    simpleDialog.setCancelable(false)
                                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    simpleDialog.setContentView(R.layout.dialog_message)
                                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                    //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                    dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                    dialogHeader.text = "Please mark your attendance."
                                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                    dialogYes.setOnClickListener({ view ->
                                        simpleDialog.cancel()
                                    })
                                    simpleDialog.show()
            //                    (mContext as DashboardActivity).showSnackMessage("Please mark your attendance")
                                } else {
                                    if (!Pref.DayStartMarked) {

                                        getLocforStart()

                                        *//*val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Are you sure ?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                getLocforStart()
                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()*//*

                        } else {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                            dialogHeader.text = "Your Day started already."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Day started already")
                        }
                    }
                }
            }
        })
*/
                    }
                }
            }

        })

        end_TV.setOnClickListener({ view ->


            var andrV = Build.VERSION.SDK_INT.toInt()
            if (andrV < 26) {
                val simpleDialogV = Dialog(mContext)
                simpleDialogV.setCancelable(false)
                simpleDialogV.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialogV.setContentView(R.layout.dialog_message)
                val dialogHeaderV =
                    simpleDialogV.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTVV =
                    simpleDialogV.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                if (Pref.user_name != null) {
                    dialog_yes_no_headerTVV.text = "Hi " + Pref.user_name!! + "!"
                } else {
                    dialog_yes_no_headerTVV.text = "Hi User" + "!"
                }
                dialogHeaderV.text = "Android Version is below 8.\n" +
                        "Functionality may not be working properly."

                val dialogYesV = simpleDialogV.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYesV.setOnClickListener({ view ->
                    simpleDialogV.cancel()
                    CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                    CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                    CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                    val stat = StatFs(Environment.getExternalStorageDirectory().path)
                    val bytesAvailable: Long
                    bytesAvailable =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            stat.blockSizeLong * stat.availableBlocksLong
                        } else {
                            stat.blockSize.toLong() * stat.availableBlocks.toLong()
                        }
                    val megAvailable = bytesAvailable / (1024 * 1024)
                    println("storage " + megAvailable.toString());
                    Timber.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())
                    if (megAvailable < 1024) {
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!
                            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader =
                            simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV =
                            simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        if (Pref.user_name != null) {
                            dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                        } else {
                            dialog_yes_no_headerTV.text = "Hi User" + "!"
                        }
                        //dialogHeader.text = "Please make sure that you have Min: 1GB. Upto 5GB(Best performance) memory available to get best login experience."
                        dialogHeader.text =
                            "Please note that memory available is less than 1 GB. App may not function properly. Please make available memory greater than 2 GB for better performance."

                        val dialogYes =
                            simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()

                            if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                                if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                    CustomDialog.getInstance(
                                        AppUtils.hiFirstNameText(),
                                        getString(R.string.battery_setting_message),
                                        "OK",
                                        "",
                                        "0",
                                        object : OnDialogCustomClickListener {
                                            override fun onOkClick() {

                                                endTvClick()

                                            }

                                            override fun onYesClick() {

                                            }

                                            override fun onNoClick() {
                                            }
                                        }).show(
                                        (mContext as DashboardActivity).supportFragmentManager,
                                        "CustomDialog"
                                    )
                                } else {
                                    endTvClick()
                                }
                            } else {
                                endTvClick()
                            }

                        })
                        simpleDialog.show()
                    } else {
                        if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                            if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                CustomDialog.getInstance(
                                    AppUtils.hiFirstNameText(),
                                    getString(R.string.battery_setting_message),
                                    "OK",
                                    "",
                                    "0",
                                    object : OnDialogCustomClickListener {
                                        override fun onOkClick() {

                                            endTvClick()

                                        }

                                        override fun onYesClick() {

                                        }

                                        override fun onNoClick() {
                                        }
                                    }).show(
                                    (mContext as DashboardActivity).supportFragmentManager,
                                    "CustomDialog"
                                )
                            } else {
                                endTvClick()
                            }
                        } else {
                            endTvClick()
                        }
                    }

                })
                simpleDialogV.show()
            } else {
                CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                val stat = StatFs(Environment.getExternalStorageDirectory().path)
                val bytesAvailable: Long
                bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    stat.blockSizeLong * stat.availableBlocksLong
                } else {
                    stat.blockSize.toLong() * stat.availableBlocks.toLong()
                }
                val megAvailable = bytesAvailable / (1024 * 1024)
                println("storage " + megAvailable.toString());
                Timber.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())
                if (megAvailable < 1024) {
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    if (Pref.user_name != null) {
                        dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                    } else {
                        dialog_yes_no_headerTV.text = "Hi User" + "!"
                    }
                    //dialogHeader.text = "Please make sure that you have Min: 1GB. Upto 5GB(Best performance) memory available to get best login experience."
                    dialogHeader.text =
                        "Please note that memory available is less than 1 GB. App may not function properly. Please make available memory greater than 2 GB for better performance."

                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()

                        if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                            if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                CustomDialog.getInstance(
                                    AppUtils.hiFirstNameText(),
                                    getString(R.string.battery_setting_message),
                                    "OK",
                                    "",
                                    "0",
                                    object : OnDialogCustomClickListener {
                                        override fun onOkClick() {

                                            endTvClick()

                                        }

                                        override fun onYesClick() {

                                        }

                                        override fun onNoClick() {
                                        }
                                    }).show(
                                    (mContext as DashboardActivity).supportFragmentManager,
                                    "CustomDialog"
                                )
                            } else {
                                endTvClick()
                            }
                        } else {
                            endTvClick()
                        }

                    })
                    simpleDialog.show()
                } else {
                    if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                        if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                            CustomDialog.getInstance(
                                AppUtils.hiFirstNameText(),
                                getString(R.string.battery_setting_message),
                                "OK",
                                "",
                                "0",
                                object : OnDialogCustomClickListener {
                                    override fun onOkClick() {

                                        endTvClick()

                                    }

                                    override fun onYesClick() {

                                    }

                                    override fun onNoClick() {
                                    }
                                }).show(
                                (mContext as DashboardActivity).supportFragmentManager,
                                "CustomDialog"
                            )
                        } else {
                            endTvClick()
                        }
                    } else {
                        endTvClick()
                    }
                }

            }


        })

        DDVisit_TV.setOnClickListener({ view ->

            var andrV = Build.VERSION.SDK_INT.toInt()
            if (andrV < 26) {
                val simpleDialogV = Dialog(mContext)
                simpleDialogV.setCancelable(false)
                simpleDialogV.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialogV.setContentView(R.layout.dialog_message)
                val dialogHeaderV =
                    simpleDialogV.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTVV =
                    simpleDialogV.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                if (Pref.user_name != null) {
                    dialog_yes_no_headerTVV.text = "Hi " + Pref.user_name!! + "!"
                } else {
                    dialog_yes_no_headerTVV.text = "Hi User" + "!"
                }
                dialogHeaderV.text = "Android Version is below 8.\n" +
                        "Functionality may not be working properly."

                val dialogYesV = simpleDialogV.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYesV.setOnClickListener({ view ->
                    simpleDialogV.cancel()
                    CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                    CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                    CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                    val stat = StatFs(Environment.getExternalStorageDirectory().path)
                    val bytesAvailable: Long
                    bytesAvailable =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            stat.blockSizeLong * stat.availableBlocksLong
                        } else {
                            stat.blockSize.toLong() * stat.availableBlocks.toLong()
                        }
                    val megAvailable = bytesAvailable / (1024 * 1024)
                    println("storage " + megAvailable.toString());
                    Timber.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())

                    if (megAvailable < 1024) {
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!
                            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader =
                            simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV =
                            simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        if (Pref.user_name != null) {
                            dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                        } else {
                            dialog_yes_no_headerTV.text = "Hi User" + "!"
                        }
                        //dialogHeader.text = "Please make sure that you have Min: 1GB. Upto 5GB(Best performance) memory available to get best login experience."
                        dialogHeader.text =
                            "Please note that memory available is less than 1 GB. App may not function properly. Please make available memory greater than 2 GB for better performance."

                        val dialogYes =
                            simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()

                            if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                                if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                    CustomDialog.getInstance(
                                        AppUtils.hiFirstNameText(),
                                        getString(R.string.battery_setting_message),
                                        "OK",
                                        "",
                                        "0",
                                        object : OnDialogCustomClickListener {
                                            override fun onOkClick() {
                                                pointTvClick()
                                            }

                                            override fun onYesClick() {

                                            }

                                            override fun onNoClick() {
                                            }
                                        }).show(
                                        (mContext as DashboardActivity).supportFragmentManager,
                                        "CustomDialog"
                                    )
                                } else {
                                    pointTvClick()
                                }
                            } else {
                                pointTvClick()
                            }

                        })
                        simpleDialog.show()
                    } else {
                        if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                            if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                CustomDialog.getInstance(
                                    AppUtils.hiFirstNameText(),
                                    getString(R.string.battery_setting_message),
                                    "OK",
                                    "",
                                    "0",
                                    object : OnDialogCustomClickListener {
                                        override fun onOkClick() {
                                            pointTvClick()
                                        }

                                        override fun onYesClick() {

                                        }

                                        override fun onNoClick() {
                                        }
                                    }).show(
                                    (mContext as DashboardActivity).supportFragmentManager,
                                    "CustomDialog"
                                )
                            } else {
                                pointTvClick()
                            }
                        } else {
                            pointTvClick()
                        }
                    }
                })
                simpleDialogV.show()
            }
            else {
                CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                val stat = StatFs(Environment.getExternalStorageDirectory().path)
                val bytesAvailable: Long
                bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    stat.blockSizeLong * stat.availableBlocksLong
                } else {
                    stat.blockSize.toLong() * stat.availableBlocks.toLong()
                }
                val megAvailable = bytesAvailable / (1024 * 1024)
                println("storage " + megAvailable.toString());
                Timber.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())

                if (megAvailable < 1024) {
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    if (Pref.user_name != null) {
                        dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                    } else {
                        dialog_yes_no_headerTV.text = "Hi User" + "!"
                    }
                    //dialogHeader.text = "Please make sure that you have Min: 1GB. Upto 5GB(Best performance) memory available to get best login experience."
                    dialogHeader.text =
                        "Please note that memory available is less than 1 GB. App may not function properly. Please make available memory greater than 2 GB for better performance."

                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()

                        if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                            if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                                CustomDialog.getInstance(
                                    AppUtils.hiFirstNameText(),
                                    getString(R.string.battery_setting_message),
                                    "OK",
                                    "",
                                    "0",
                                    object : OnDialogCustomClickListener {
                                        override fun onOkClick() {
                                            pointTvClick()
                                        }

                                        override fun onYesClick() {

                                        }

                                        override fun onNoClick() {
                                        }
                                    }).show(
                                    (mContext as DashboardActivity).supportFragmentManager,
                                    "CustomDialog"
                                )
                            } else {
                                pointTvClick()
                            }
                        } else {
                            pointTvClick()
                        }

                    })
                    simpleDialog.show()
                } else {
                    if (Pref.BatterySettingGlobal && Pref.BatterySetting) {
                        if (AppUtils.getBatteryPercentage(mContext).toInt() <= 15) {
                            CustomDialog.getInstance(
                                AppUtils.hiFirstNameText(),
                                getString(R.string.battery_setting_message),
                                "OK",
                                "",
                                "0",
                                object : OnDialogCustomClickListener {
                                    override fun onOkClick() {
                                        pointTvClick()
                                    }

                                    override fun onYesClick() {

                                    }

                                    override fun onNoClick() {
                                    }
                                }).show(
                                (mContext as DashboardActivity).supportFragmentManager,
                                "CustomDialog"
                            )
                        } else {
                            pointTvClick()
                        }
                    } else {
                        pointTvClick()
                    }
                }
            }


        })
        /*   end_TV.setOnClickListener({ view ->
            //faceDetectorSetUp()
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            } else if(isEndCall==false && Pref.DayEndMarked == false && Pref.DayStartMarked ==true){

                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_yes_no)
                val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                dialogHeader.text = "Are you sure to Exit for the Day?"
                val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                    //if(Pref.IsshowDayStartSelfie){
                    //isCameraDayStart=true
                    //initPermissionCheck()
                    //}else{
                    progress_wheel.spin()
                    isCalledFromStart=false
                    getPicUrl()
                    //}
                })
                dialogNo.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
            } else {
                if (Pref.DayEndMarked) {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                    dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                    dialogHeader.text = "Your Day already ended..."
                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Day already ended")
                } else {
                    //Pref.IsDDvistedOnceByDay=true
                    if (Pref.DayStartMarked) {

                        getLocforEnd()

//                        if (Pref.IsDDvistedOnceByDay) {// 01-09-2021  end date time , point visit not checking
                            *//*val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Are you sure to Exit for the Day?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()

                                getLocforEnd()



                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()*//*





//                        }
                       *//* else {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Please visit Point at once..."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
                        }*//*

                    } else {
                        // 27-08-21 For ITC
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                        dialogHeader.text = "Please start your day..."
                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Please start your day")
                        //test
                    }
                }
            }
        })

        DDVisit_TV.setOnClickListener({ view ->
            if (!AppUtils.isOnline(mContext) && false) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            } else {
                if (!Pref.isAddAttendence) {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = "Hi "+Pref.user_name+"!"
                    dialogHeader.text = "Please Marked Your Attendance First..."
                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Please Marked Your Attendance First")
                } else {
                    if (!Pref.IsDDvistedOnceByDay) {
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_yes_no)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() +"!"
                        dialogHeader.text = "Wish to Visit Point Location Right Now?"
                        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                        val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                            getLocforDD()
                        })
                        dialogNo.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
                    } else {
                        // 27-08-21 For ITC
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                        dialogHeader.text = "You are already visited once..."
                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
//                            (mContext as DashboardActivity).showSnackMessage("You are already visited once...")
                    }

                }
            }
        })
*/
        faceDetectorSetUp()

        /*if (Pref.isReplaceShopText)
            tv_shop.text = getString(R.string.customers)
        else
            tv_shop.text = getString(R.string.shops)*/

        tv_shop.text = Pref.shopText + "(s)"

        if (Pref.willShowUpdateDayPlan)
            tv_view_all.visibility = View.VISIBLE
        else
            tv_view_all.visibility = View.GONE

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
        Timber.e("=======INIT VIEW (DASHBOARD FRAGMENT)=======")
        Timber.e("Today's Shop Visit Count====> $todaysShopVisitCount")

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        history_ll.setOnClickListener(this)
        shop_ll.setOnClickListener(this)
        attandance_ll.setOnClickListener(this)
        order_ll.setOnClickListener(this)
        //shops_RL.setOnClickListener(this)
        //time_RL.setOnClickListener(this)
        //price_RL.setOnClickListener(this)
        ll_dash_total_order_newD.setOnClickListener(this)
        ll_dash_total_mew_order_newD.setOnClickListener(this)
        order_ll_new.setOnClickListener(this)

        tv_view_all.setOnClickListener(this)
        tv_pick_date_range.setOnClickListener(this)
        n_shops_TV.setOnClickListener(this)
        n_time_TV.setOnClickListener(this)

        fab_bot.setCustomClickListener {
            (mContext as DashboardActivity).showLanguageAlert(false)
        }

        best_performing_shop_TV.text = getString(R.string.todays_task)

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        //addShopDBModel()

        /*val list=AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingDate(AppUtils.getCurrentDate())

        if (list==null || list.isEmpty()) {
            avgOrder.text = "0"
        }
        else {
            var totalAmount = 0.0

            for (i in list.indices) {
                totalAmount += list[i].total_price?.toDouble()!!
            }
            //val totalPrice = DecimalFormat("##.##").format(totalAmount)
            val totalPrice = String.format("%.2f", totalAmount.toFloat())
            itemView.tv_total_amount.text = context.getString(R.string.rupee_symbol)+totalPrice
        }*/

        if (Pref.isAttendanceFeatureOnly) {
            ll_attendance_report_main.visibility = View.VISIBLE
            rl_dashboard_fragment_main.visibility = View.GONE

            //getAttendanceReport(AppUtils.getCurrentDateForShopActi())

        } else {
            ll_attendance_report_main.visibility = View.GONE
            rl_dashboard_fragment_main.visibility = View.VISIBLE
        }

        /*if (Pref.isOrderShow) {
            order_ll.visibility = View.VISIBLE
            price_RL.visibility = View.VISIBLE
        } else {
            order_ll.visibility = View.GONE
            price_RL.visibility = View.GONE
        }*/

        if (!Pref.isMeetingAvailable && !Pref.isShopAddEditAvailable)
            fab.visibility = View.GONE
        else
            fab.visibility = View.VISIBLE


        if (Pref.isServiceFeatureEnable) {
            tv_order.text = getString(R.string.myjobs)
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.GONE
        }
        else if (Pref.willActivityShow) {
            tv_order.text = getString(R.string.activities)
            no_of_order_TV.text = getString(R.string.today_activity)
            avgOrder.text = InfoWizard.getActivityForToday()
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.VISIBLE
        }
        else {
            shop_tome_order_tab_LL.visibility = View.VISIBLE
            if (Pref.isQuotationShow) {
                tv_order.text = getString(R.string.quotation)
                no_of_order_TV.text = getString(R.string.total_quot)
                avgOrder.text =
                    getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
                iv_order_icon.visibility = View.GONE
                iv_quot_icon.visibility = View.VISIBLE
            } else {
                no_of_order_TV.text = getString(R.string.total_order_value_new)
                avgOrder.text =
                    getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
                iv_order_icon.visibility = View.VISIBLE
                iv_quot_icon.visibility = View.GONE

                if (Pref.isOrderReplacedWithTeam) {
                    tv_order.text = getString(R.string.team_details)
                    iv_order_icon.setImageResource(R.drawable.ic_team_icon)
                    //price_RL.visibility = View.GONE
                    ll_dash_total_order_newD.visibility = View.GONE
                } else {
                    tv_order.text = getString(R.string.orders)
                    iv_order_icon.setImageResource(R.drawable.ic_dashboard_order_icon)

                    if (Pref.isOrderShow) {
                        order_ll.visibility = View.VISIBLE
                        //price_RL.visibility = View.VISIBLE
                        //avgOrder.visibility = View.VISIBLE
                        //no_of_order_TV.visibility = View.VISIBLE
                        ll_dash_total_order_newD.visibility = View.VISIBLE
                    } else {
                        order_ll.visibility = View.GONE
                        //price_RL.visibility = View.GONE
                        //avgOrder.visibility = View.GONE
                        //no_of_order_TV.visibility = View.GONE
                        ll_dash_total_order_newD.visibility = View.GONE
                    }
                }
            }
        }

        if (Pref.isChatBotShow)
            fab_bot.visibility = View.VISIBLE
        else
            fab_bot.visibility = View.GONE



        if (Pref.isShowTimeline)
            history_ll.visibility = View.VISIBLE
        else
            history_ll.visibility = View.GONE

        //Added setting Login 12-08-21
        if (Pref.IsShowPartyOnAppDashboard)
            shop_ll.visibility = View.VISIBLE
        else
            shop_ll.visibility = View.GONE

        //Pref.IsShowAttendanceOnAppDashboard = true
        if (Pref.IsShowAttendanceOnAppDashboard)
            attandance_ll.visibility = View.VISIBLE
        else
            attandance_ll.visibility = View.GONE

        //Pref.IsShowTotalVisitsOnAppDashboard = true
        //Pref.IsShowVisitDurationOnAppDashboard = true

        if (Pref.IsShowTotalVisitsOnAppDashboard) {
//            shops_RL.visibility = View.VISIBLE
            //n_shops_TV.visibility = View.VISIBLE
            //no_of_shop_TV.visibility = View.VISIBLE
            ll_dash_total_visit_newD.visibility = View.VISIBLE
        } else {
//            shops_RL.visibility = View.GONE
            //n_shops_TV.visibility = View.GONE
            //no_of_shop_TV.visibility = View.GONE
            ll_dash_total_visit_newD.visibility = View.GONE
        }
        if (Pref.IsAttendVisitShowInDashboardGlobal) {
            if (Pref.IsAttendVisitShowInDashboard) {
                ll_dash_visit_attendance_newD.visibility = View.VISIBLE
            } else {
                ll_dash_visit_attendance_newD.visibility = View.GONE
            }
        } else {
            ll_dash_visit_attendance_newD.visibility = View.GONE
        }

        if (Pref.IsShowVisitDurationOnAppDashboard) {
//            time_RL.visibility = View.VISIBLE
            //n_time_TV.visibility = View.VISIBLE
            //no_of_time_TV.visibility = View.VISIBLE
            ll_dash_visit_duration_newD.visibility = View.VISIBLE
        } else {
//            time_RL.visibility = View.GONE
            //n_time_TV.visibility = View.GONE
            //no_of_time_TV.visibility = View.GONE
            ll_dash_visit_duration_newD.visibility = View.GONE
        }

        //Pref.IsDMS = true
        //19-08-21
        if (Pref.IsShowMarkDistVisitOnDshbrd) {
            //DDVisit_TV.visibility = View.VISIBLE
            //DDVisitdate_TV.visibility = View.VISIBLE
            ll_dash_point_visit_newD.visibility = View.VISIBLE
        } else {
            //DDVisit_TV.visibility = View.GONE
            //DDVisitdate_TV.visibility = View.GONE
            ll_dash_point_visit_newD.visibility = View.GONE
        }

        if (Pref.IsShowDayStart) {
            //StartRL.visibility = View.VISIBLE
            //start_shop.visibility = View.VISIBLE
            ll_dash_day_start_newD.visibility = View.VISIBLE
        } else {
            //StartRL.visibility = View.GONE
            //start_shop.visibility = View.GONE
            ll_dash_day_start_newD.visibility = View.GONE

        }
        if (Pref.IsAttendVisitShowInDashboardGlobal) {
            if (Pref.IsAttendVisitShowInDashboard) {
                ll_dash_visit_attendance_newD.visibility = View.VISIBLE
            } else {
                ll_dash_visit_attendance_newD.visibility = View.GONE
            }
        } else {
            ll_dash_visit_attendance_newD.visibility = View.GONE
        }

        if (Pref.IsShowDayEnd) {
            //endRL.visibility = View.VISIBLE
            //enddate_TV.visibility = View.VISIBLE
            ll_dash_day_end_newD.visibility = View.VISIBLE
        } else {
            //endRL.visibility = View.GONE
            //enddate_TV.visibility = View.GONE
            ll_dash_day_end_newD.visibility = View.GONE
        }
        if(Pref.ShowPartyWithCreateOrder && Pref.ShowUserwisePartyWithCreateOrder){
            ll_dash_total_mew_order_newD.visibility = View.VISIBLE
            order_ll_new.visibility = View.VISIBLE
        }else{
            ll_dash_total_mew_order_newD.visibility = View.GONE
            order_ll_new.visibility = View.GONE
        }

        if (Pref.ShowAutoRevisitInDashboard)
            revisit_ll.visibility = View.VISIBLE
        else
            revisit_ll.visibility = View.GONE

        //test code
        /*val counter = object: CountUpTimer(80000, 1){

            override fun onCount(count: Int) {
                println("timet_tag Counting: $count")
                try{
                    Pref.TimerSec = count
                    if( count % 60 == 0){
                        Pref.TimerSec = 0
                        Pref.TimerMin = Pref.TimerMin + 1
                    }
                    if(Pref.TimerMin == 61){
                        Pref.TimerMin = 0
                        Pref.TimerHr =  Pref.TimerHr + 1
                    }

                    best_performing_shop_TV.text = "${Pref.TimerHr.toInt()}:${Pref.TimerMin.toInt()}:${Pref.TimerSec.toInt()}"
                }catch (ex:Exception){
                    println("timet_tag Counting: ex")
                    ex.printStackTrace()
                }
            }

            override fun onFinish() {
                println("timet_tag Counting done")
            }
        }
        counter.start()*/
        //test code

        initAdapter()
        initBottomAdapter()
    }

    abstract class CountUpTimer(private val secondsInFuture: Int, countUpIntervalSeconds: Int) : CountDownTimer(secondsInFuture.toLong() * 1000, countUpIntervalSeconds.toLong() * 1000) {

        abstract fun onCount(count: Int)

        override fun onTick(msUntilFinished: Long) {
            onCount(((secondsInFuture.toLong() * 1000 - msUntilFinished) / 1000).toInt())
        }
    }


    private fun getAttendanceReport(date: String) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var isApiCalling = true

        val repository = AttendanceReportRepoProvider.provideAttendanceReportRepository()
        progress_wheel_attendance.spin()
        BaseActivity.compositeDisposable.add(
            repository.getAttendanceReportList(date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                /*.doOnTerminate {
                            Log.e("Dashboard", "===========On Terminate Attendance Report==========")
                            if (isApiCalling)
                                getAttendanceReport(date)
                        }*/
                .subscribe({ result ->
                    val attendanceList = result as AttendanceReportDataModel

                    isApiCalling = false

                    when {
                        attendanceList.status == NetworkConstant.SUCCESS -> {
                            progress_wheel_attendance.stopSpinning()

                            if (attendanceList.attendance_report_list != null && attendanceList.attendance_report_list!!.size > 0) {

                                tv_no_data_available.visibility = View.GONE
                                rv_attendance_report_list.adapter = AttendanceReportAdapter(
                                    mContext,
                                    attendanceList.attendance_report_list,
                                    object : AttendanceReportAdapter.OnClickListener {
                                        override fun onCallClick(adapterPosition: Int) {
                                            if (TextUtils.isEmpty(attendanceList.attendance_report_list!![adapterPosition].contact_no) || attendanceList.attendance_report_list!![adapterPosition].contact_no.equals(
                                                    "null",
                                                    ignoreCase = true
                                                )
                                                || !AppUtils.isValidateMobile(attendanceList.attendance_report_list!![adapterPosition].contact_no!!)
                                            ) {
                                                (mContext as DashboardActivity).showSnackMessage(
                                                    getString(R.string.error_phn_no_unavailable)
                                                )
                                            } else {
                                                IntentActionable.initiatePhoneCall(
                                                    mContext,
                                                    attendanceList.attendance_report_list!![adapterPosition].contact_no
                                                )
                                            }
                                        }
                                    })
                            } else
                                tv_no_data_available.visibility = View.VISIBLE

                        }
                        attendanceList.status == NetworkConstant.SESSION_MISMATCH -> {
                            progress_wheel_attendance.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                            startActivity(
                                Intent(
                                    mContext as DashboardActivity,
                                    LoginActivity::class.java
                                )
                            )
                            (mContext as DashboardActivity).overridePendingTransition(0, 0)
                            (mContext as DashboardActivity).finish()
                        }
                        attendanceList.status == NetworkConstant.NO_DATA -> {
                            progress_wheel_attendance.stopSpinning()
                            tv_no_data_available.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)

                        }
                        else -> {
                            progress_wheel_attendance.stopSpinning()
                            tv_no_data_available.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                        }
                    }

                }, { error ->
                    isApiCalling = false
                    progress_wheel_attendance.stopSpinning()
                    error.printStackTrace()
                    tv_no_data_available.visibility = View.VISIBLE
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )
    }


    var shopDurationData: ShopDurationRequestData? = null
    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.revisit_ll -> {
                //if (!Pref.isAddAttendence) {
                if (!Pref.isAddAttendence && false){
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                }
                else {
                    if (Pref.IsShowDayStart && !Pref.DayStartMarked) {
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!
                            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader =
                            simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV =
                            simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                        dialogHeader.text = "Please start your day..."
                        val dialogYes =
                            simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
                    }
                    else {
                        progress_wheel.spin()
                        revisit_ll.isEnabled=false
                        //checkAutoRevisit()

                        var loc:Location = Location("")
                        loc.latitude=Pref.current_latitude.toDouble()
                        loc.longitude=Pref.current_longitude.toDouble()
                        checkAutoRevisitManual(loc)

                        Handler().postDelayed(Runnable {
                            revisit_ll.isEnabled=true
                            progress_wheel.stopSpinning()
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!
                                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader =
                                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV =
                                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Process has been successfully completed."
                            val dialogYes =
                                simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
                        }, 5000)
                    }
                }
            }





            R.id.fab -> {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else {
                    /*if (!Pref.isMeetingAvailable)
                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
                    else {
                        CommonDialog.getInstance("Action", "What you like to do?", "Add Shop", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
                            override fun onLeftClick() {
                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
                            }

                            override fun onRightClick(editableData: String) {
                                showAddMeetingAlert()
                            }

                        }).show((mContext as DashboardActivity).supportFragmentManager, "")
                    }*/

                    if (Pref.IsShowDayStart) {
                        if (!Pref.DayStartMarked) {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!
                                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader =
                                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV =
                                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                            //dialogHeader.text = "Please start your day..."
                            dialogHeader.text =
                                "Mark your Day Start and after the same only, you can Add new shops. Thanks."
                            val dialogYes =
                                simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                            (mContext as DashboardActivity).showSnackMessage("Please start your day")
                            return
                        }
                    }


                    if (Pref.isMeetingAvailable && Pref.isShopAddEditAvailable) {
                        CommonDialog.getInstance(
                            "Select Activity",
                            "What will you be doing now?",
                            "Add a ${Pref.shopText}",
                            Pref.meetingText,
                            false,
                            false,
                            true,
                            object : CommonDialogClickListener {
                                override fun onLeftClick() {
                                    (mContext as DashboardActivity).loadFragment(
                                        FragType.AddShopFragment,
                                        true,
                                        ""
                                    )
                                }

                                override fun onRightClick(editableData: String) {
                                    showAddMeetingAlert()
                                }

                            }).show((mContext as DashboardActivity).supportFragmentManager, "")
                    } else if (Pref.isMeetingAvailable)
                        showAddMeetingAlert()
                    else if (Pref.isShopAddEditAvailable)
                        (mContext as DashboardActivity).loadFragment(
                            FragType.AddShopFragment,
                            true,
                            ""
                        )
                }
            }

//            R.id.fab -> {
//                //19-08-21 customer shop create stop untill daystart
//                if (Pref.IsShowDayStart) {
//                    if (!Pref.DayStartMarked) {
//                        (mContext as DashboardActivity).showSnackMessage("Please start your day")
//                    } else {
//                        if (!Pref.isAddAttendence)
//                            (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
//                        else {
//                            /*if (!Pref.isMeetingAvailable)
//                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                            else {
//                                CommonDialog.getInstance("Action", "What you like to do?", "Add Shop", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                    override fun onLeftClick() {
//                                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                    }
//
//                                    override fun onRightClick(editableData: String) {
//                                        showAddMeetingAlert()
//                                    }
//
//                                }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                            }*/
//
//                            if (Pref.isMeetingAvailable && Pref.isShopAddEditAvailable) {
//                                CommonDialog.getInstance("Select Activity", "What will you be doing now?", "Add a ${Pref.shopText}", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                    override fun onLeftClick() {
//                                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                    }
//
//                                    override fun onRightClick(editableData: String) {
//                                        showAddMeetingAlert()
//                                    }
//
//                                }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                            } else if (Pref.isMeetingAvailable)
//                                showAddMeetingAlert()
//                            else if (Pref.isShopAddEditAvailable)
//                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                        }
//                    }
//                }
//                else
//                    if (!Pref.isAddAttendence)
//                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
//                    else {
//                        /*if (!Pref.isMeetingAvailable)
//                            (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                        else {
//                            CommonDialog.getInstance("Action", "What you like to do?", "Add Shop", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                override fun onLeftClick() {
//                                    (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                }
//
//                                override fun onRightClick(editableData: String) {
//                                    showAddMeetingAlert()
//                                }
//
//                            }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                        }*/
//
//                        if (Pref.isMeetingAvailable && Pref.isShopAddEditAvailable) {
//                            CommonDialog.getInstance("Select Activity", "What will you be doing now?", "Add a ${Pref.shopText}", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                override fun onLeftClick() {
//                                    (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                }
//
//                                override fun onRightClick(editableData: String) {
//                                    showAddMeetingAlert()
//                                }
//
//                            }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                        } else if (Pref.isMeetingAvailable)
//                            showAddMeetingAlert()
//                        else if (Pref.isShopAddEditAvailable)
//                            (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                    }
//            }


            R.id.history_ll -> {
                (mContext as DashboardActivity).isMemberMap = false
                if (!Pref.willTimelineWithFixedLocationShow)
                    (mContext as DashboardActivity).loadFragment(
                        FragType.OrderhistoryFragment,
                        false,
                        ""
                    )
                else
                    (mContext as DashboardActivity).loadFragment(
                        FragType.TimeLineFragment,
                        false,
                        ""
                    )

            }
            R.id.cancel_timer -> {
                if (!tv_timer.text.toString().trim().equals("00.00.00")) {
                    tv_timer.text = "00.00.00"
                } else {
                    ll_recorder_root.visibility = View.GONE
                    DashboardFragment.isRecordRootVisible = false
                    (mContext as DashboardActivity).updateScreenRecStatus()
                }
            }
            R.id.pause_record -> {
                if (hbRecorder != null) {
                    if (hbRecorder!!.isBusyRecording) {
                        if (hbRecorder!!.isRecordingPaused) {
                            hbRecorder!!.resumeScreenRecording()
                            ScreenRecService.isPause = false
                            pause_record.text = "||"
                        } else {
                            hbRecorder!!.pauseScreenRecording()
                            ScreenRecService.isPause = true
                            pause_record.text = ">"
                        }

                    }
                }
            }
            R.id.iv_screen_status -> {
                if (hbRecorder != null) {
                    if (hbRecorder!!.isBusyRecording) {
                        pause_record.visibility = View.GONE
                        hbRecorder!!.stopScreenRecording()
                    } else {
                        pause_record.visibility = View.VISIBLE
                        startRecordingScreen()
                    }
                } else {
                    pause_record.visibility = View.VISIBLE
                    startRecordingScreen()
                }

            }
            R.id.shop_ll -> {

                /*val list = ArrayList<ShopDurationRequestData>()

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_89"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_82"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_897"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-29T00:00:02"
                shopDurationData?.visited_time = "2019-01-29T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-28T00:00:02"
                shopDurationData?.visited_time = "2019-01-28T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                Log.e("Dashboard", "Duplicate array list size===> " + list.size)*/

                //val newShopList = FTStorageUtils.removeDuplicateData(list)

                //val hashSet = HashSet<ShopDurationRequestData>()
                /*hashSet.addAll(list)
                list.clear()
                list.addAll(hashSet)*/

                /*val newShopList = ArrayList<ShopDurationRequestData>()
                for (i in list.indices){
                    if (hashSet.add(list[i]))
                        newShopList.add(list[i])
                }*/

                //Log.e("Dashboard", "Unique new list size===> " + newShopList.size)

                if (!Pref.isShowShopBeatWise) {
                    (mContext as DashboardActivity).isShopFromChatBot = false
                    if (!Pref.isServiceFeatureEnable)
                        (mContext as DashboardActivity).loadFragment(
                            FragType.NearByShopsListFragment,
                            false,
                            ""
                        )
                    else
                        (mContext as DashboardActivity).loadFragment(
                            FragType.CustomerListFragment,
                            false,
                            ""
                        )
                } else
                    (mContext as DashboardActivity).loadFragment(
                        FragType.BeatListFragment,
                        false,
                        ""
                    )
                //(mContext as DashboardActivity).loadFragment(FragType.NewNearByShopsListFragment, false, "")

            }
            R.id.attandance_ll -> {
                (mContext as DashboardActivity).isChatBotAttendance = false
                (mContext as DashboardActivity).loadFragment(FragType.AttendanceFragment, false, "")

            }
            R.id.order_ll -> {
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                when {
                    Pref.isServiceFeatureEnable -> {
                        Pref.IsMyJobFromTeam = false
                        (mContext as DashboardActivity).loadFragment(
                            FragType.MyJobsFragment,
                            false,
                            ""
                        )
                    }
                    Pref.willActivityShow -> (mContext as DashboardActivity).loadFragment(
                        FragType.ActivityShopListFragment,
                        false,
                        ""
                    )
                    Pref.isQuotationShow -> {
                        (mContext as DashboardActivity).isBack = false
                        (mContext as DashboardActivity).loadFragment(
                            FragType.QuotationListFragment,
                            false,
                            ""
                        )
                    }
                    Pref.isOrderReplacedWithTeam -> (mContext as DashboardActivity).loadFragment(
                        FragType.MemberListFragment,
                        true,
                        Pref.user_id!!
                    )
                    else -> {
                        (mContext as DashboardActivity).isOrderFromChatBot = false
                        (mContext as DashboardActivity).loadFragment(
                            FragType.NewOrderListFragment,
                            false,
                            ""
                        )
                    }

                }

                //(mContext as DashboardActivity).loadFragment(FragType.OrderListFragment, false, "")
            }
            //R.id.shops_RL -> {
            R.id.n_shops_TV -> {
                (mContext as DashboardActivity).loadFragment(FragType.AverageShopFragment, true, "")
            }
            //R.id.time_RL -> {
            R.id.n_time_TV -> {
                (mContext as DashboardActivity).loadFragment(
                    FragType.AvgTimespentShopListFragment,
                    true,
                    ""
                )
            }
            R.id.ll_dash_total_mew_order_newD ->{
                (mContext as DashboardActivity).loadFragment(FragType.ViewNewOrdHistoryFrag, true, "")
            }
            R.id.order_ll_new ->{
                (mContext as DashboardActivity).loadFragment(FragType.ViewNewOrdHisAllFrag, true, "")
            }

            //R.id.price_RL -> {
            R.id.ll_dash_total_order_newD -> {
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

                if (Pref.IsShowDayEnd) {
                    return
                }

                when {
                    //Pref.isServiceFeatureEnable -> (mContext as DashboardActivity).loadFragment(FragType.MyJobsFragment, true, "")
                    Pref.willActivityShow -> (mContext as DashboardActivity).loadFragment(
                        FragType.DateWiseActivityListFragment,
                        true,
                        ""
                    )
                    Pref.isQuotationShow -> (mContext as DashboardActivity).loadFragment(
                        FragType.DateWiseQuotationList,
                        true,
                        ""
                    )
                    else -> (mContext as DashboardActivity).loadFragment(
                        FragType.NewDateWiseOrderListFragment,
                        true,
                        ""
                    )
                }
            }

            R.id.tv_view_all -> {
                (mContext as DashboardActivity).loadFragment(FragType.AllShopListFragment, true, "")
            }
            R.id.tv_pick_date_range -> {
                val datePicker = android.app.DatePickerDialog(
                    mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            R.id.fab_bot -> {
                (mContext as DashboardActivity).showLanguageAlert(false)
            }
            R.id.ll_dash_visit_attendance_newD -> {
                if (AppUtils.isOnline(mContext)) {
                    if(Pref.PartyUpdateAddrMandatory){
                        var isDDLatLongNull=true
                        var assignDD  = AppDatabase.getDBInstance()!!.ddListDao().getAll()
                        try{
                            for (i in assignDD.indices) {
                                if(!assignDD[i].dd_latitude.toString().equals("0") && !assignDD[i].dd_longitude.toString().equals("0")){
                                    isDDLatLongNull=false
                                }
                            }
                        }catch (ex:Exception){
                            ex.printStackTrace()
                        }
                        if(isDDLatLongNull && assignDD.size>0){
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(true)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message_broad)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                            dialogHeader.text="You must update WD Point address from Dashboard > Customer > Update Address."

                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                Handler().postDelayed(Runnable {
                                    (mContext as DashboardActivity).loadFragment(FragType.NearByShopsListFragment, false, "")
                                }, 100)
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
                        }else{
                            (mContext as DashboardActivity).loadFragment(FragType.PhotoAttendanceFragment, false, "")
                        }
                    }else{
                        (mContext as DashboardActivity).loadFragment(FragType.PhotoAttendanceFragment, false, "")
                    }

                } else {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                }
            }
        }
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        getAttendanceReport(AppUtils.getFormattedDateForApi(myCalendar.time))
    }


    private fun showAddMeetingAlert() {
        AddMeetingDialog.getInstance(Pref.meetingText, "", "Cancel", "Confirm", false, true,
            false, object : AddMeetingDialog.OnButtonClickListener {
                override fun onLeftClick() {
                }

                override fun onRightClick(editableData: String, selectedTypeId: String) {

                    val list =
                        AppDatabase.getDBInstance()!!.addMeetingDao().durationAvailable(false)
                    if (list != null) {
                        for (i in 0 until list.size) {
                            val endTimeStamp = System.currentTimeMillis().toString()
                            val duration =
                                AppUtils.getTimeFromTimeSpan(list[i].startTimeStamp!!, endTimeStamp)
                            AppDatabase.getDBInstance()!!.addMeetingDao().updateEndTimeOfMeeting(
                                endTimeStamp,
                                list[i].id,
                                AppUtils.getCurrentDateForShopActi()
                            )
                            AppDatabase.getDBInstance()!!.addMeetingDao()
                                .updateTimeDurationForDayOfMeeting(
                                    list[i].id,
                                    duration,
                                    AppUtils.getCurrentDateForShopActi()
                                )
                            AppDatabase.getDBInstance()!!.addMeetingDao().updateDurationAvailable(
                                true,
                                list[i].id,
                                AppUtils.getCurrentDateForShopActi()
                            )
                        }
                    }

                    val meetingEntity = MeetingEntity()
                    meetingEntity.date = AppUtils.getCurrentDateForShopActi()
                    meetingEntity.duration_spent = "00:00:00"
                    meetingEntity.remakrs = editableData
                    meetingEntity.startTimeStamp = System.currentTimeMillis().toString()
                    meetingEntity.date_time = AppUtils.getCurrentISODateTime()
                    meetingEntity.lattitude = Pref.current_latitude
                    meetingEntity.longitude = Pref.current_longitude
                    meetingEntity.meetingTypeId = selectedTypeId

                    var address = LocationWizard.getAdressFromLatlng(
                        mContext,
                        meetingEntity.lattitude?.toDouble(),
                        meetingEntity.longitude?.toDouble()
                    )

                    if (address.contains("http"))
                        address = "Unknown"

                    meetingEntity.address = address
                    meetingEntity.pincode = LocationWizard.getPostalCode(
                        mContext,
                        meetingEntity.lattitude?.toDouble()!!,
                        meetingEntity.longitude?.toDouble()!!
                    )

                    var distance = 0.0

                    if (Pref.isOnLeave.equals("false", ignoreCase = true)) {

                        Timber.e("=====User is at work (At meeting revisit time)=======")

                        val userlocation = UserLocationDataEntity()
                        userlocation.latitude = meetingEntity.lattitude!!
                        userlocation.longitude = meetingEntity.longitude!!

                        var loc_distance = 0.0

                        val locationList = AppDatabase.getDBInstance()!!.userLocationDataDao()
                            .getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

                        if (locationList != null && locationList.isNotEmpty()) {
                            loc_distance = LocationWizard.getDistance(
                                locationList[locationList.size - 1].latitude.toDouble(),
                                locationList[locationList.size - 1].longitude.toDouble(),
                                userlocation.latitude.toDouble(),
                                userlocation.longitude.toDouble()
                            )
                        }
                        val finalDistance = (Pref.tempDistance.toDouble() + loc_distance).toString()

                        Timber.e("===Distance (At meeting revisit time)===")
                        Timber.e("Temp Distance====> " + Pref.tempDistance)
                        Timber.e("Normal Distance====> $loc_distance")
                        Timber.e("Total Distance====> $finalDistance")
                        Timber.e("=====================================")

                        userlocation.distance = finalDistance
                        userlocation.locationName = LocationWizard.getNewLocationName(
                            mContext,
                            userlocation.latitude.toDouble(),
                            userlocation.longitude.toDouble()
                        )
                        userlocation.timestamp = LocationWizard.getTimeStamp()
                        userlocation.time = LocationWizard.getFormattedTime24Hours(true)
                        userlocation.meridiem = LocationWizard.getMeridiem()
                        userlocation.hour = LocationWizard.getHour()
                        userlocation.minutes = LocationWizard.getMinute()
                        userlocation.isUploaded = false
                        userlocation.shops = AppDatabase.getDBInstance()!!.shopActivityDao()
                            .getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toString()
                        userlocation.updateDate = AppUtils.getCurrentDateForShopActi()
                        userlocation.updateDateTime = AppUtils.getCurrentDateTime()
                        userlocation.meeting = AppDatabase.getDBInstance()!!.addMeetingDao()
                            .getMeetingDateWise(AppUtils.getCurrentDateForShopActi()).size.toString()
                        userlocation.network_status =
                            if (AppUtils.isOnline(mContext)) "Online" else "Offline"
                        userlocation.battery_percentage =
                            AppUtils.getBatteryPercentage(mContext).toString()

                        //harcoded location isUploaded true begin
                        userlocation.isUploaded = true
                        //harcoded location isUploaded true end

                        AppDatabase.getDBInstance()!!.userLocationDataDao().insertAll(userlocation)

                        Pref.totalS2SDistance =
                            (Pref.totalS2SDistance.toDouble() + userlocation.distance.toDouble()).toString()

                        distance = Pref.totalS2SDistance.toDouble()
                        Pref.totalS2SDistance = "0.0"
                        Pref.tempDistance = "0.0"
                    } else {
                        Timber.e("=====User is on leave (At meeting revisit time)=======")
                        distance = 0.0
                    }

                    meetingEntity.distance_travelled = distance.toString()

                    AppDatabase.getDBInstance()!!.addMeetingDao().insertAll(meetingEntity)
                }

            }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    @SuppressLint("WrongConstant")
    private fun initAdapter() {
        mRouteActivityDashboardAdapter = RouteActivityDashboardAdapter(
            this.context!!,
            AppDatabase.getDBInstance()!!.userLocationDataDao().all
        )
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
    }


    private fun getLocationList(): List<UserLocationDataEntity> {
        return AppDatabase.getDBInstance()!!.userLocationDataDao().all

    }

    fun UpdateLocationData() {
        mRouteActivityDashboardAdapter.update(getLocationList())
        mRouteActivityDashboardAdapter.notifyDataSetChanged()
    }

    @SuppressLint("WrongConstant")
    public fun initBottomAdapter() {

        /*val performList = ArrayList<AddShopDBModelEntity>()
        val updatedPerformList = ArrayList<AddShopDBModelEntity>()

        for (i in list.indices) {
            if (i == 0)
                performList.add(list[i])
            else {
                if (list[i].shop_id != list[i - 1].shop_id) {
                    performList.add(list[i])
                }
            }
        }


        for (i in performList.indices) {
            if (i > 4)
                break

            updatedPerformList.add(performList[i])
        }

        if (updatedPerformList.size == 1)
            best_performing_shop_TV.text = "Best performing " + updatedPerformList.size + " shop"
        else
            best_performing_shop_TV.text = "Best performing " + updatedPerformList.size + " shops"*/


        val work_type_list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getTodaysData(AppUtils.getCurrentDate()) as ArrayList<SelectedWorkTypeEntity>

        if (work_type_list != null && work_type_list.size > 0) {
            no_shop_tv.visibility = View.GONE
            reportList.visibility = View.VISIBLE
            //adapter = ReportAdapter(mContext, work_type_list)
            layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
            reportList.layoutManager = layoutManager
            //reportList.adapter = TodaysWorkAdapter(mContext, work_type_list)
            reportList.adapter = TodaysWorkAdapter1(mContext, work_type_list)
            reportList.isNestedScrollingEnabled = false
        } else {
            reportList.visibility = View.GONE
        }

        if (Pref.isAttendanceFeatureOnly)
            getAttendanceReport(AppUtils.getCurrentDateForShopActi())
        else {
            //getUserPjpList(work_type_list)
            val pjpList =
                AppDatabase.getDBInstance()?.pjpListDao()?.getAll() as ArrayList<PjpListEntity>

            if (pjpList != null && pjpList.isNotEmpty()) {
                no_shop_tv.visibility = View.GONE
                rv_pjp_list.visibility = View.VISIBLE

                rv_pjp_list.adapter = PjpAdapter(mContext, pjpList, object : PJPClickListner {
                    override fun visitShop(shop: Any) {
                        if (!Pref.isAddAttendence)
                            (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                        else {
                            val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                            (mContext as DashboardActivity).callShopVisitConfirmationDialog(
                                nearbyShop.shopName,
                                nearbyShop.shop_id
                            )
                        }
                    }
                })

            } else {
                rv_pjp_list.visibility = View.GONE

                if (work_type_list == null || work_type_list.size == 0)
                    no_shop_tv.visibility = View.VISIBLE
            }
        }

        if(Pref.IsShowWorkType){
            reportList.visibility = View.VISIBLE
        }else{
            reportList.visibility = View.GONE
        }
    }

    private fun getUserPjpList(workTypeList: ArrayList<SelectedWorkTypeEntity>) {
        if(!Pref.isActivatePJPFeature){
            return
        }
        Timber.d("PJP api DashF1 PJPDetails/PJPList call")
        if (!AppUtils.isOnline(mContext)) {

            if (workTypeList == null || workTypeList.size == 0)
                no_shop_tv.visibility = View.VISIBLE

            rv_pjp_list.visibility = View.GONE
            //(mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
            repository.getUserPJPList(AppUtils.getCurrentDateForShopActi())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as UserPjpResponseModel
                    Timber.d("GET USER PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                    progress_wheel.stopSpinning()
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.pjp_list != null && response.pjp_list.size > 0) {
                            no_shop_tv.visibility = View.GONE
                            rv_pjp_list.visibility = View.VISIBLE
                            //rv_pjp_list.adapter = PjpAdapter(mContext, response.pjp_list)

                        } else {
                            rv_pjp_list.visibility = View.GONE
                            //(mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (workTypeList == null || workTypeList.size == 0)
                                no_shop_tv.visibility = View.VISIBLE
                        }
                    } else {
                        rv_pjp_list.visibility = View.GONE
                        //(mContext as DashboardActivity).showSnackMessage(response.message!!)

                        if (workTypeList == null || workTypeList.size == 0)
                            no_shop_tv.visibility = View.VISIBLE
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    Timber.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                    error.printStackTrace()
                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    rv_pjp_list.visibility = View.GONE

                    if (workTypeList == null || workTypeList.size == 0)
                        no_shop_tv.visibility = View.VISIBLE
                })
        )
    }

    fun addShopDBModel() {

        try {
            val endDate = AppUtils.convertDateStringToLong(AppUtils.getCurrentDateForShopActi())
            val startDate =
                AppUtils.convertDateStringToLong(AppUtils.getThreeMonthsPreviousDate(AppUtils.getCurrentDateForShopActi()))

            val orderList = AppDatabase.getDBInstance()!!.orderListDao()
                .getListAccordingToDateRange(startDate, endDate) as ArrayList<OrderListEntity>

            /*val hashSet = HashSet<OrderListEntity>()
            hashSet.addAll(orderList)
            orderList.clear()
            orderList.addAll(hashSet)*/

            list = ArrayList<AddShopDBModelEntity>()

            /*if (orderList != null && orderList.isNotEmpty()) {

                no_shop_tv.visibility = View.GONE

                for (i in orderList.indices) {
                    val maxAmount = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAmountAccordingToShopId(orderList[i].shop_id!!)

                    if (!TextUtils.isEmpty(maxAmount)) {
                        val mAddShopDBModelEntity = AddShopDBModelEntity()
                        mAddShopDBModelEntity.shop_id = orderList[i].shop_id
                        mAddShopDBModelEntity.address = orderList[i].address
                        mAddShopDBModelEntity.shopName = orderList[i].shop_name
                        mAddShopDBModelEntity.orderValue = maxAmount.toFloat().toInt() //orderList[i].order_amount?.toFloat()!!.toInt()
                        list.add(mAddShopDBModelEntity)
                    }
                }

                Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
                    override fun compare(p0: AddShopDBModelEntity?, p1: AddShopDBModelEntity?): Int {
                        return p0?.orderValue?.let { p1?.orderValue?.compareTo(it) }!!
                    }
                })

            } else {*/
            //best_performing_shop_TV.text = /*"Best Performing 0 shop"*/ getString(R.string.best_performing_shop)
            //no_shop_tv.visibility = View.VISIBLE
            val mAddShopDBModelEntity1: AddShopDBModelEntity = AddShopDBModelEntity()
            mAddShopDBModelEntity1.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity1.shopName = "The Tommy Hilfinger"
            mAddShopDBModelEntity1.orderValue = 200

            val mAddShopDBModelEntity2: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity2.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity2.shopName = "Addidus Store"
            mAddShopDBModelEntity2.orderValue = 300


            val mAddShopDBModelEntity3: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity3.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity3.shopName = "Turtle Outlet"
            mAddShopDBModelEntity3.orderValue = 400

            val mAddShopDBModelEntity4: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity4.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity4.shopName = "Levice International"
            mAddShopDBModelEntity4.orderValue = 500

            val mAddShopDBModelEntity5: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity5.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity5.shopName = "Image Kolkata Store"
            mAddShopDBModelEntity5.orderValue = 600

            val mAddShopDBModelEntity6: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity5.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity5.shopName = "Image Kolkata Store"
            mAddShopDBModelEntity5.orderValue = 600


            list.add(mAddShopDBModelEntity1)
            list.add(mAddShopDBModelEntity2)
            list.add(mAddShopDBModelEntity3)
            list.add(mAddShopDBModelEntity4)
            list.add(mAddShopDBModelEntity5)
            list.add(mAddShopDBModelEntity6)


            Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
                override fun compare(p0: AddShopDBModelEntity?, p1: AddShopDBModelEntity?): Int {
                    return p0?.orderValue?.let { p1?.orderValue?.compareTo(it) }!!
                }
            })

            //}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateItem() {
        //addShopDBModel()
        //initBottomAdapter()

        if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals(
                "false",
                ignoreCase = true
            )
        ) {
            val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
            if (Pref.isAddAttendence && (list == null || list.isEmpty()))
                getSelectedRouteList()
            else
                initBottomAdapter()
        } else
            initBottomAdapter()
    }

    fun updateBottomList() {
        if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals(
                "false",
                ignoreCase = true
            )
        )
            getSelectedRouteList()
        else {
            initBottomAdapter()
            checkToCallMemberList()
        }
    }

    private fun getSelectedRouteList() {
        val repository = GetRouteListRepoProvider.routeListRepoProvider()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()

        BaseActivity.compositeDisposable.add(
            repository.routeList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as SelectedRouteListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val workTypeList = response.worktype

                        if (workTypeList != null && workTypeList.isNotEmpty()) {

                            AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()
                            AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.deleteData()
                            AppDatabase.getDBInstance()?.selectedRouteListDao()?.deleteRoute()

                            doAsync {

                                for (i in workTypeList.indices) {
                                    val selectedwortkType = SelectedWorkTypeEntity()
                                    selectedwortkType.ID = workTypeList[i].id?.toInt()!!
                                    selectedwortkType.Descrpton = workTypeList[i].name
                                    selectedwortkType.date = AppUtils.getCurrentDate()
                                    AppDatabase.getDBInstance()?.selectedWorkTypeDao()
                                        ?.insertAll(selectedwortkType)
                                }

                                val routeList = response.route_list
                                if (routeList != null && routeList.isNotEmpty()) {
                                    for (i in routeList.indices) {
                                        val selectedRoute = SelectedRouteEntity()
                                        selectedRoute.route_id = routeList[i].id
                                        selectedRoute.route_name = routeList[i].route_name
                                        selectedRoute.date = AppUtils.getCurrentDate()

                                        val routeShopList = routeList[i].shop_details_list
                                        if (routeShopList != null && routeShopList.size > 0) {
                                            for (j in routeShopList.indices) {
                                                val selectedRouteShop =
                                                    SelectedRouteShopListEntity()
                                                selectedRouteShop.route_id = routeList[i].id
                                                selectedRouteShop.shop_address =
                                                    routeShopList[j].shop_address
                                                selectedRouteShop.shop_contact_no =
                                                    routeShopList[j].shop_contact_no
                                                selectedRouteShop.shop_name =
                                                    routeShopList[j].shop_name
                                                selectedRouteShop.shop_id = routeShopList[j].shop_id
                                                selectedRouteShop.date = AppUtils.getCurrentDate()
                                                AppDatabase.getDBInstance()
                                                    ?.selectedRouteShopListDao()
                                                    ?.insert(selectedRouteShop)
                                            }
                                        }

                                        AppDatabase.getDBInstance()?.selectedRouteListDao()
                                            ?.insert(selectedRoute)
                                    }
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    initBottomAdapter()
                                    checkToCallMemberList()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            checkToCallMemberList()
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        checkToCallMemberList()
                    }

                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    checkToCallMemberList()
                })
        )
    }

    fun sendHomeLoc(locationInfoModel: locationInfoModel?) {

        if (AppUtils.isOnline(mContext))
            callSubmitLocApi(locationInfoModel)
        else {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

            Handler().postDelayed(Runnable {
                (mContext as DashboardActivity).checkToShowHomeLocationAlert()
            }, 200)
        }
    }

    private fun callSubmitLocApi(locationInfoModel: locationInfoModel?) {

        if (TextUtils.isEmpty(Pref.session_token) || TextUtils.isEmpty(Pref.user_id)) {
            startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
            (mContext as DashboardActivity).overridePendingTransition(0, 0)
            (mContext as DashboardActivity).finish()
        }

        val submitLoc = SubmitHomeLocationInputModel()
        submitLoc.session_token = Pref.session_token!!
        submitLoc.user_id = Pref.user_id!!

        if (!TextUtils.isEmpty(locationInfoModel?.latitude))
            submitLoc.latitude = locationInfoModel?.latitude!!

        if (!TextUtils.isEmpty(locationInfoModel?.longitude))
            submitLoc.longitude = locationInfoModel?.longitude!!

        if (!TextUtils.isEmpty(locationInfoModel?.address))
            submitLoc.address = locationInfoModel?.address!!

        if (!TextUtils.isEmpty(locationInfoModel?.city))
            submitLoc.city = locationInfoModel?.city!!

        if (!TextUtils.isEmpty(locationInfoModel?.state))
            submitLoc.state = locationInfoModel?.state!!

        if (!TextUtils.isEmpty(locationInfoModel?.pinCode))
            submitLoc.pincode = locationInfoModel?.pinCode!!

        val repository = SubmitHomeLocationRepoProvider.submitHomeLocRepo()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
            repository.submitAttendance(submitLoc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    progress_wheel.stopSpinning()
                    if (response.status == NetworkConstant.SUCCESS) {
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        Pref.isHomeLocAvailable = true
                        Pref.home_latitude = submitLoc.latitude
                        Pref.home_longitude = submitLoc.longitude
                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()

                    } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
//                                (mContext as DashboardActivity).clearData()
                        startActivity(
                            Intent(
                                mContext as DashboardActivity,
                                LoginActivity::class.java
                            )
                        )
                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                        (mContext as DashboardActivity).finish()
                    } else {
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        Handler().postDelayed(Runnable {
                            (mContext as DashboardActivity).checkToShowHomeLocationAlert()
                        }, 200)
                    }
                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    Handler().postDelayed(Runnable {
                        (mContext as DashboardActivity).checkToShowHomeLocationAlert()
                    }, 200)
                })
        )
    }


    private fun checkToCallMemberList() {
        Timber.e("==============checkToCallMemberList(Dashboard Fragment)==============")
        Handler().postDelayed(Runnable {
            if (Pref.isOfflineTeam && Pref.isAddAttendence) {
                var progress_wheel: ProgressWheel? = null
                if (Pref.isAttendanceFeatureOnly)
                    progress_wheel = progress_wheel_attendance
                else
                    progress_wheel = this.progress_wheel

                progress_wheel?.spin()

                doAsync {

                    val list = AppDatabase.getDBInstance()?.memberDao()?.getAll()

                    uiThread {
                        progress_wheel.stopSpinning()
                        if (list == null || list.isEmpty())
                            callMemberListApi()
                    }
                }
            }
        }, 200)
    }

    private fun callMemberListApi() {
        //return
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }


        if (customProgressDialog.isAdded)
            return

        Timber.e("==============call offline member api(Dashboard Fragment)==============")

        val repository = TeamRepoProvider.teamRepoProvider()
        customProgressDialog.show((mContext as DashboardActivity).supportFragmentManager, "")
        BaseActivity.compositeDisposable.add(
            repository.offlineTeamList("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as TeamListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.member_list != null && response.member_list!!.isNotEmpty()) {

                            doAsync {

                                response.member_list?.forEach {
                                    val member = MemberEntity()
                                    AppDatabase.getDBInstance()?.memberDao()
                                        ?.insertAll(member.apply {
                                            user_id = it.user_id
                                            user_name = it.user_name
                                            contact_no = it.contact_no
                                            super_id = it.super_id
                                            super_name = it.super_name
                                            date_time = AppUtils.getCurrentISODateTime()
                                        })
                                }

                                uiThread {
                                    //callMemberShopListApi()
                                    customProgressDialog.dismiss()
                                }
                            }
                        } else {
                            //callMemberShopListApi()
                            customProgressDialog.dismiss()
                        }

                    } else if (response.status == NetworkConstant.NO_DATA) {
                        //callMemberShopListApi()
                        customProgressDialog.dismiss()
                    } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
//                                (mContext as DashboardActivity).clearData()
                        customProgressDialog.dismiss()
                        startActivity(
                            Intent(
                                mContext as DashboardActivity,
                                LoginActivity::class.java
                            )
                        )
                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                        (mContext as DashboardActivity).finish()
                    } else {
                        customProgressDialog.dismiss()
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    }

                }, { error ->
                    customProgressDialog.dismiss()
                    error.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )
    }

    private fun callMemberShopListApi() {

        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
            repository.offlineTeamShopList("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as TeamShopListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.shop_list != null && response.shop_list!!.isNotEmpty()) {

                            doAsync {

                                response.shop_list?.forEach {
                                    val memberShop = MemberShopEntity()
                                    AppDatabase.getDBInstance()?.memberShopDao()
                                        ?.insertAll(memberShop.apply {
                                            user_id = it.user_id
                                            shop_id = it.shop_id
                                            shop_name = it.shop_name
                                            shop_lat = it.shop_lat
                                            shop_long = it.shop_long
                                            shop_address = it.shop_address
                                            shop_pincode = it.shop_pincode
                                            shop_contact = it.shop_contact
                                            total_visited = it.total_visited
                                            last_visit_date = it.last_visit_date
                                            shop_type = it.shop_type
                                            dd_name = it.dd_name
                                            entity_code = it.entity_code
                                            model_id = it.model_id
                                            primary_app_id = it.primary_app_id
                                            secondary_app_id = it.secondary_app_id
                                            lead_id = it.lead_id
                                            funnel_stage_id = it.funnel_stage_id
                                            stage_id = it.stage_id
                                            booking_amount = it.booking_amount
                                            type_id = it.type_id
                                            area_id = it.area_id
                                            assign_to_pp_id = it.assign_to_pp_id
                                            assign_to_dd_id = it.assign_to_dd_id
                                            isUploaded = true
                                            date_time = AppUtils.getCurrentISODateTime()
                                        })
                                }

                                uiThread {
                                    customProgressDialog.dismiss()
                                }
                            }
                        }

                    } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
//                                (mContext as DashboardActivity).clearData()
                        customProgressDialog.dismiss()
                        startActivity(
                            Intent(
                                mContext as DashboardActivity,
                                LoginActivity::class.java
                            )
                        )
                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                        (mContext as DashboardActivity).finish()
                    } else {
                        customProgressDialog.dismiss()
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    }

                }, { error ->
                    customProgressDialog.dismiss()
                    error.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )
    }

    fun updateUi() {

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
        Timber.e("=======UPDATE UI FOR AUTO REVISIT (DASHBOARD FRAGMENT)=======")
        Timber.e("Today's Shop Visit Count====> $todaysShopVisitCount")

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        when {
            Pref.willActivityShow -> avgOrder.text = InfoWizard.getActivityForToday()
            Pref.isQuotationShow -> avgOrder.text =
                getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
            else -> avgOrder.text =
                getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
        }

        UpdateLocationData()

        if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals(
                "false",
                ignoreCase = true
            )
        ) {
            val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
            if (Pref.isAddAttendence && (list == null || list.isEmpty()))
                getSelectedRouteList()
            else
                initBottomAdapter()
        } else
            initBottomAdapter()
    }

    fun refresh() {
        //checkToCallAssignedDDListApi()
        Timber.d("tag_dash_refresh refresh call")
        var shopDeactiveList=AppDatabase.getDBInstance()?.shopDeactivateDao()!!.getAll()
        if(shopDeactiveList!=null && shopDeactiveList.size>0){
            Timber.d("tag_dash_refresh refresh call if")
            for(i in 0..shopDeactiveList.size-1){
                var notificationManager = (mContext as DashboardActivity).getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(shopDeactiveList.get(i).noti_id!!.toInt())
                updateShopStatusFromNoti(shopDeactiveList.get(i).shop_id!!)
            }
        }else{
            Timber.d("tag_dash_refresh refresh call else")
            checkToCallAssignedDDListApi()
        }

    }

    private fun updateShopStatusFromNoti(shopID:String){
        AppDatabase.getDBInstance()!!.addShopEntryDao().updateShopStatus(shopID,"0")
        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(0,shopID)
        if(AppUtils.isOnline(mContext))
            convertToReqAndApiCallForShopStatus(AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopID!!))
    }
    private fun convertToReqAndApiCallForShopStatus(addShopData: AddShopDBModelEntity) {
        if (Pref.user_id == null || Pref.user_id == "" || Pref.user_id == " ") {
            (mContext as DashboardActivity).showSnackMessage("Please login again")
            BaseActivity.isApiInitiated = false
            return
        }

        val addShopReqData = AddShopRequestData()
        addShopReqData.session_token = Pref.session_token
        addShopReqData.address = addShopData.address
        addShopReqData.actual_address = addShopData.address
        addShopReqData.owner_contact_no = addShopData.ownerContactNumber
        addShopReqData.owner_email = addShopData.ownerEmailId
        addShopReqData.owner_name = addShopData.ownerName
        addShopReqData.pin_code = addShopData.pinCode
        addShopReqData.shop_lat = addShopData.shopLat.toString()
        addShopReqData.shop_long = addShopData.shopLong.toString()
        addShopReqData.shop_name = addShopData.shopName.toString()
        addShopReqData.shop_id = addShopData.shop_id
        addShopReqData.added_date = ""
        addShopReqData.user_id = Pref.user_id
        addShopReqData.type = addShopData.type
        addShopReqData.assigned_to_pp_id = addShopData.assigned_to_pp_id
        addShopReqData.assigned_to_dd_id = addShopData.assigned_to_dd_id
        /*addShopReqData.dob = addShopData.dateOfBirth
        addShopReqData.date_aniversary = addShopData.dateOfAniversary*/
        addShopReqData.amount = addShopData.amount
        addShopReqData.area_id = addShopData.area_id
        /*val addShop = AddShopRequest()
        addShop.data = addShopReqData*/

        addShopReqData.model_id = addShopData.model_id
        addShopReqData.primary_app_id = addShopData.primary_app_id
        addShopReqData.secondary_app_id = addShopData.secondary_app_id
        addShopReqData.lead_id = addShopData.lead_id
        addShopReqData.stage_id = addShopData.stage_id
        addShopReqData.funnel_stage_id = addShopData.funnel_stage_id
        addShopReqData.booking_amount = addShopData.booking_amount
        addShopReqData.type_id = addShopData.type_id

        if (!TextUtils.isEmpty(addShopData.dateOfBirth))
            addShopReqData.dob =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.dateOfBirth)

        if (!TextUtils.isEmpty(addShopData.dateOfAniversary))
            addShopReqData.date_aniversary =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.dateOfAniversary)

        addShopReqData.director_name = addShopData.director_name
        addShopReqData.key_person_name = addShopData.person_name
        addShopReqData.phone_no = addShopData.person_no

        if (!TextUtils.isEmpty(addShopData.family_member_dob))
            addShopReqData.family_member_dob =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.family_member_dob)

        if (!TextUtils.isEmpty(addShopData.add_dob))
            addShopReqData.addtional_dob =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.add_dob)

        if (!TextUtils.isEmpty(addShopData.add_doa))
            addShopReqData.addtional_doa =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.add_doa)

        addShopReqData.specialization = addShopData.specialization
        addShopReqData.category = addShopData.category
        addShopReqData.doc_address = addShopData.doc_address
        addShopReqData.doc_pincode = addShopData.doc_pincode
        addShopReqData.is_chamber_same_headquarter = addShopData.chamber_status.toString()
        addShopReqData.is_chamber_same_headquarter_remarks = addShopData.remarks
        addShopReqData.chemist_name = addShopData.chemist_name
        addShopReqData.chemist_address = addShopData.chemist_address
        addShopReqData.chemist_pincode = addShopData.chemist_pincode
        addShopReqData.assistant_contact_no = addShopData.assistant_no
        addShopReqData.average_patient_per_day = addShopData.patient_count
        addShopReqData.assistant_name = addShopData.assistant_name

        if (!TextUtils.isEmpty(addShopData.doc_family_dob))
            addShopReqData.doc_family_member_dob =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.doc_family_dob)

        if (!TextUtils.isEmpty(addShopData.assistant_dob))
            addShopReqData.assistant_dob =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.assistant_dob)

        if (!TextUtils.isEmpty(addShopData.assistant_doa))
            addShopReqData.assistant_doa =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.assistant_doa)

        if (!TextUtils.isEmpty(addShopData.assistant_family_dob))
            addShopReqData.assistant_family_dob =
                AppUtils.changeAttendanceDateFormatToCurrent(addShopData.assistant_family_dob)

        addShopReqData.entity_id = addShopData.entity_id
        addShopReqData.party_status_id = addShopData.party_status_id
        addShopReqData.retailer_id = addShopData.retailer_id
        addShopReqData.dealer_id = addShopData.dealer_id
        addShopReqData.beat_id = addShopData.beat_id
        addShopReqData.assigned_to_shop_id = addShopData.assigned_to_shop_id
        //addShopReqData.actual_address = addShopData.actual_address


        if(addShopData.shopStatusUpdate.equals("0"))
            addShopReqData.shopStatusUpdate = addShopData.shopStatusUpdate
        else
            addShopReqData.shopStatusUpdate = "1"

        callEditShopApiForShopStatus(addShopReqData)
    }

    private fun callEditShopApiForShopStatus(addShopReqData: AddShopRequestData) {
        if (BaseActivity.isApiInitiated)
            return

        BaseActivity.isApiInitiated = true

        progress_wheel.spin()

        if (true) {
            val repository = EditShopRepoProvider.provideEditShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                repository.editShop(addShopReqData)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val addShopResult = result as AddShopResponse
                        Timber.d("Edit Shop : " + ", SHOP: " + addShopReqData.shop_name + ", RESPONSE:" + result.message)
                        if (addShopResult.status == NetworkConstant.SUCCESS) {
                            AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopReqData.shop_id)
                            AppDatabase.getDBInstance()?.shopDeactivateDao()!!.deleteByShopID(addShopReqData.shop_id!!)
                            progress_wheel.stopSpinning()
                            refresh()
                        }
                        BaseActivity.isApiInitiated = false
                    }, { error ->
                        error.printStackTrace()
                        BaseActivity.isApiInitiated = false
                        progress_wheel.stopSpinning()
                        checkToCallAssignedDDListApi()
                    })
            )
        }
    }

    private fun checkToCallAssignedDDListApi() {
        Timber.d("tag_dash_refresh checkToCallAssignedDDListApi call")
        if (!TextUtils.isEmpty(Pref.profile_state)){
            Timber.d("tag_dash_refresh checkToCallAssignedDDListApi call if")
            getAssignedDDListApi()
        }
        else {
            Timber.d("tag_dash_refresh checkToCallAssignedDDListApi call else")
            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                getProductList("")
            else
                getProductList(
                    AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date
                )
        }
    }

    private fun getAssignedDDListApi() {
        Timber.d("tag_itc_check assignToDDList call DashboardFragment")
        val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
            repository.assignToDDList(Pref.profile_state)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as AssignToDDListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val list = response.assigned_to_dd_list

                        if (list != null && list.isNotEmpty()) {

                            doAsync {

                                val assignDDList =
                                    AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                if (assignDDList != null)
                                    AppDatabase.getDBInstance()?.ddListDao()?.delete()

                                for (i in list.indices) {
                                    val assignToDD = AssignToDDEntity()
                                    assignToDD.dd_id = list[i].assigned_to_dd_id
                                    assignToDD.dd_name = list[i].assigned_to_dd_authorizer_name
                                    assignToDD.dd_phn_no = list[i].phn_no
                                    assignToDD.pp_id = list[i].assigned_to_pp_id
                                    assignToDD.type_id = list[i].type_id
                                    assignToDD.dd_latitude = list[i].dd_latitude
                                    assignToDD.dd_longitude = list[i].dd_longitude
                                    AppDatabase.getDBInstance()?.ddListDao()?.insert(assignToDD)
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    if (!TextUtils.isEmpty(Pref.profile_state))
                                        getAssignedPPListApi()
                                    else {
                                        if (AppDatabase.getDBInstance()?.productListDao()
                                                ?.getAll()!!.isEmpty()
                                        )
                                            getProductList("")
                                        else
                                            getProductList(
                                                AppDatabase.getDBInstance()?.productListDao()
                                                    ?.getAll()?.get(0)?.date
                                            )
                                    }
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            if (!TextUtils.isEmpty(Pref.profile_state))
                                getAssignedPPListApi()
                            else {
                                if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!
                                        .isEmpty()
                                )
                                    getProductList("")
                                else
                                    getProductList(
                                        AppDatabase.getDBInstance()?.productListDao()?.getAll()
                                            ?.get(0)?.date
                                    )
                            }
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        if (!TextUtils.isEmpty(Pref.profile_state))
                            getAssignedPPListApi()
                        else {
                            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                getProductList("")
                            else
                                getProductList(
                                    AppDatabase.getDBInstance()?.productListDao()?.getAll()
                                        ?.get(0)?.date
                                )
                        }
                    }

                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    if (!TextUtils.isEmpty(Pref.profile_state))
                        getAssignedPPListApi()
                    else {
                        if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                            getProductList("")
                        else
                            getProductList(
                                AppDatabase.getDBInstance()?.productListDao()?.getAll()
                                    ?.get(0)?.date
                            )
                    }
                })
        )
    }

    private fun getAssignedPPListApi() {
        val repository = AssignToPPListRepoProvider.provideAssignPPListRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
            repository.assignToPPList(Pref.profile_state)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as AssignToPPListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val list = response.assigned_to_pp_list

                        if (list != null && list.isNotEmpty()) {

                            doAsync {

                                val assignPPList =
                                    AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                if (assignPPList != null)
                                    AppDatabase.getDBInstance()?.ppListDao()?.delete()

                                for (i in list.indices) {
                                    val assignToPP = AssignToPPEntity()
                                    assignToPP.pp_id = list[i].assigned_to_pp_id
                                    assignToPP.pp_name = list[i].assigned_to_pp_authorizer_name
                                    assignToPP.pp_phn_no = list[i].phn_no
                                    AppDatabase.getDBInstance()?.ppListDao()?.insert(assignToPP)
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getAssignedToShopApi()
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        getAssignedToShopApi()
                    }

                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    getAssignedToShopApi()
                })
        )
    }

    private fun getAssignedToShopApi() {
        Timber.d("tag_itc_check getAssignedToShopList call DashboardFragment")
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.assignToShopList(Pref.profile_state)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as AssignedToShopListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val list = response.shop_list

                        AppDatabase.getDBInstance()?.assignToShopDao()?.delete()

                        doAsync {
                            list?.forEach {
                                val shop = AssignToShopEntity()
                                AppDatabase.getDBInstance()?.assignToShopDao()?.insert(shop.apply {
                                    assigned_to_shop_id = it.assigned_to_shop_id
                                    name = it.name
                                    phn_no = it.phn_no
                                    type_id = it.type_id
                                })
                            }

                            uiThread {
                                progress_wheel.stopSpinning()
                                if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!
                                        .isEmpty()
                                )
                                    getProductList("")
                                else
                                    getProductList(
                                        AppDatabase.getDBInstance()?.productListDao()?.getAll()
                                            ?.get(0)?.date
                                    )
                            }
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                            getProductList("")
                        else
                            getProductList(
                                AppDatabase.getDBInstance()?.productListDao()?.getAll()
                                    ?.get(0)?.date
                            )
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                        getProductList("")
                    else
                        getProductList(
                            AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date
                        )
                })
        )
    }


    private fun getProductList(date: String?) {
        Timber.d("tag_dash_refresh getProductList call")
        if(Pref.isOrderShow){
            Timber.d("tag_dash_refresh getProductList call if")
            val repository = ProductListRepoProvider.productListProvider()
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel

            progress_wheel?.spin()
            Timber.d("api_call_dash  getProductList()")
            BaseActivity.compositeDisposable.add(
                //repository.getProductList(Pref.session_token!!, Pref.user_id!!, date!!)
                repository.getProductList(Pref.session_token!!, Pref.user_id!!, "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as ProductListResponseModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            val list = response.product_list

                            if (list != null && list.isNotEmpty()) {

                                doAsync {

                                    if (!TextUtils.isEmpty(date))
                                        AppDatabase.getDBInstance()?.productListDao()
                                            ?.deleteAllProduct()

                                    AppDatabase.getDBInstance()?.productListDao()?.insertAll(list!!)

                                    /*for (i in list.indices) {
                                                val productEntity = ProductListEntity()
                                                productEntity.id = list[i].id?.toInt()!!
                                                productEntity.product_name = list[i].product_name
                                                productEntity.watt = list[i].watt
                                                productEntity.category = list[i].category
                                                productEntity.brand = list[i].brand
                                                productEntity.brand_id = list[i].brand_id
                                                productEntity.watt_id = list[i].watt_id
                                                productEntity.category_id = list[i].category_id
                                                productEntity.date = AppUtils.getCurrentDateForShopActi()
                                                AppDatabase.getDBInstance()?.productListDao()?.insert(productEntity)
                                            }*/

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getSelectedRouteListRefresh()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getSelectedRouteListRefresh()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getSelectedRouteListRefresh()
                        }

                    }, { error ->
                        error.printStackTrace()
                        progress_wheel.stopSpinning()
                        getSelectedRouteListRefresh()
                    })
            )
        }else{
            getSelectedRouteListRefresh()
        }
    }




    private fun getSelectedRouteListRefresh() {
        Timber.d("getSelectedRouteListRefresh call")
        val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
        if (list != null && list.isNotEmpty()) {
            AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()
            AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.deleteData()
            AppDatabase.getDBInstance()?.selectedRouteListDao()?.deleteRoute()
        }

        val repository = GetRouteListRepoProvider.routeListRepoProvider()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        Timber.d("api_call_dash  routeList()")

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
            repository.routeList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as SelectedRouteListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val workTypeList = response.worktype

                        if (workTypeList != null && workTypeList.isNotEmpty()) {

                            doAsync {

                                for (i in workTypeList.indices) {
                                    val selectedwortkType = SelectedWorkTypeEntity()
                                    selectedwortkType.ID = workTypeList[i].id?.toInt()!!
                                    selectedwortkType.Descrpton = workTypeList[i].name
                                    selectedwortkType.date = AppUtils.getCurrentDate()
                                    AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.insertAll(selectedwortkType)
                                }

                                val routeList = response.route_list
                                if (routeList != null && routeList.isNotEmpty()) {
                                    for (i in routeList.indices) {
                                        val selectedRoute = SelectedRouteEntity()
                                        selectedRoute.route_id = routeList[i].id
                                        selectedRoute.route_name = routeList[i].route_name
                                        selectedRoute.date = AppUtils.getCurrentDate()

                                        val routeShopList = routeList[i].shop_details_list
                                        if (routeShopList != null && routeShopList.size > 0) {
                                            for (j in routeShopList.indices) {
                                                val selectedRouteShop =
                                                    SelectedRouteShopListEntity()
                                                selectedRouteShop.route_id = routeList[i].id
                                                selectedRouteShop.shop_address =
                                                    routeShopList[j].shop_address
                                                selectedRouteShop.shop_contact_no =
                                                    routeShopList[j].shop_contact_no
                                                selectedRouteShop.shop_name =
                                                    routeShopList[j].shop_name
                                                selectedRouteShop.shop_id = routeShopList[j].shop_id
                                                selectedRouteShop.date = AppUtils.getCurrentDate()
                                                AppDatabase.getDBInstance()
                                                    ?.selectedRouteShopListDao()
                                                    ?.insert(selectedRouteShop)
                                            }
                                        }

                                        AppDatabase.getDBInstance()?.selectedRouteListDao()?.insert(selectedRoute)
                                    }
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    callUserConfigApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            callUserConfigApi()
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        callUserConfigApi()
                    }

                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    callUserConfigApi()
                })
        )
    }

    private fun callUserConfigApi() {
        val repository = UserConfigRepoProvider.provideUserConfigRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        Timber.d("api_call_dash  userConfig()")
        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
            repository.userConfig(Pref.user_id!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as UserConfigResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {

                        try {

                            Log.e(
                                "Dashboard",
                                "willLeaveApprovalEnable================> " + Pref.willLeaveApprovalEnable
                            )


                            if (response.getconfigure != null && response.getconfigure!!.size > 0) {
                                for (i in response.getconfigure!!.indices) {
                                    if (response.getconfigure!![i].Key.equals(
                                            "isVisitSync",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            AppUtils.isVisitSync =
                                                response.getconfigure!![i].Value!!
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isAddressUpdate",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            AppUtils.isAddressUpdated =
                                                response.getconfigure!![i].Value!!
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willShowUpdateDayPlan",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willShowUpdateDayPlan =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "updateDayPlanText",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.updateDayPlanText =
                                                response.getconfigure!![i].Value!!
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "dailyPlanListHeaderText",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.dailyPlanListHeaderText =
                                                response.getconfigure!![i].Value!!
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isRateNotEditable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isRateNotEditable =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isMeetingAvailable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isMeetingAvailable =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "willShowTeamDetails",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willShowTeamDetails =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isAllowPJPUpdateForTeam",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isAllowPJPUpdateForTeam =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willLeaveApprovalEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willLeaveApprovalEnable =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "willReportShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willReportShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "willAttendanceReportShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willAttendanceReportShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "willPerformanceReportShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willPerformanceReportShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "willVisitReportShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willVisitReportShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "attendance_text",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.attendance_text =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "willTimesheetShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willTimesheetShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isAttendanceFeatureOnly",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isAttendanceFeatureOnly =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "iscollectioninMenuShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isCollectioninMenuShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isVisitShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isVisitShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isOrderShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isOrderShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isShopAddEditAvailable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShopAddEditAvailable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isEntityCodeVisible",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isEntityCodeVisible =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isAreaMandatoryInPartyCreation",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isAreaMandatoryInPartyCreation =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isShowPartyInAreaWiseTeam",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowPartyInAreaWiseTeam =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isChangePasswordAllowed",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isChangePasswordAllowed =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isQuotationShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isQuotationShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isQuotationPopupShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isQuotationPopupShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isHomeRestrictAttendance",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isHomeRestrictAttendance =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "homeLocDistance",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.homeLocDistance =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "shopLocAccuracy",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.shopLocAccuracy =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isMultipleAttendanceSelection",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isMultipleAttendanceSelection =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isOrderReplacedWithTeam",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isOrderReplacedWithTeam =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isDDShowForMeeting",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isDDShowForMeeting =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isDDMandatoryForMeeting",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isDDMandatoryForMeeting =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isOfflineTeam",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isOfflineTeam =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isAllTeamAvailable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isAllTeamAvailable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isNextVisitDateMandatory",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isNextVisitDateMandatory =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isRecordAudioEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isRecordAudioEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isShowCurrentLocNotifiaction",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowCurrentLocNotifiaction =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isUpdateWorkTypeEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isUpdateWorkTypeEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isAchievementEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isAchievementEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isTarVsAchvEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isTarVsAchvEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isLeaveEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isLeaveEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isOrderMailVisible",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isOrderMailVisible =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isShopEditEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShopEditEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isTaskEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isTaskEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "isAppInfoEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isAppInfoEnable =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "appInfoMins",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.appInfoMins =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                    } else if (response.getconfigure!![i].Key.equals("autoRevisitDistance", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.autoRevisitDistance = response.getconfigure!![i].Value!!
                                    }
                                    else if (response.getconfigure!![i].Key.equals("autoRevisitTime", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.autoRevisitTime =
                                                response.getconfigure!![i].Value!!
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willAutoRevisitEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willAutoRevisitEnable =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "dynamicFormName",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.dynamicFormName =
                                                response.getconfigure!![i].Value!!
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willDynamicShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willDynamicShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willActivityShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willActivityShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willMoreVisitUpdateCompulsory",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willMoreVisitUpdateCompulsory =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willMoreVisitUpdateOptional",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willMoreVisitUpdateOptional =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isDocumentRepoShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isDocumentRepoShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isChatBotShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isChatBotShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isAttendanceBotShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isAttendanceBotShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isVisitBotShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isVisitBotShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowOrderRemarks",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isShowOrderRemarks =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowOrderSignature",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isShowOrderSignature =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowSmsForParty",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isShowSmsForParty =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isVisitPlanShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isVisitPlanShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isVisitPlanMandatory",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isVisitPlanMandatory =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isAttendanceDistanceShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isAttendanceDistanceShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willTimelineWithFixedLocationShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willTimelineWithFixedLocationShow =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowTimeline",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isShowTimeline =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willScanVisitingCard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willScanVisitingCard =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isCreateQrCode",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isCreateQrCode =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isScanQrForRevisit",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isScanQrForRevisit =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowLogoutReason",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.isShowLogoutReason =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willShowHomeLocReason",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willShowHomeLocReason =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willShowShopVisitReason",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willShowShopVisitReason =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "minVisitDurationSpentTime",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.minVisitDurationSpentTime =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willShowPartyStatus",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                            Pref.willShowPartyStatus =
                                                response.getconfigure!![i].Value == "1"
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "willShowEntityTypeforShop",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.willShowEntityTypeforShop =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowRetailerEntity",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowRetailerEntity =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowDealerForDD",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowDealerForDD =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowBeatGroup",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowBeatGroup =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowShopBeatWise",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowShopBeatWise =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowBankDetailsForShop",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowBankDetailsForShop =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowOTPVerificationPopup",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowOTPVerificationPopup =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "locationTrackInterval",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.locationTrackInterval =
                                                response.getconfigure!![i].Value!!
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowMicroLearning",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowMicroLearning =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "homeLocReasonCheckMins",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.homeLocReasonCheckMins =
                                                response.getconfigure!![i].Value!!
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "currentLocationNotificationMins",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.currentLocationNotificationMins =
                                                response.getconfigure!![i].Value!!
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isMultipleVisitEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isMultipleVisitEnable =
                                                response.getconfigure!![i].Value!! == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowVisitRemarks",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowVisitRemarks =
                                                response.getconfigure!![i].Value!! == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShowNearbyCustomer",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShowNearbyCustomer =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isServiceFeatureEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isServiceFeatureEnable =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isPatientDetailsShowInOrder",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isPatientDetailsShowInOrder =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isPatientDetailsShowInCollection",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isPatientDetailsShowInCollection =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isShopImageMandatory",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.isShopImageMandatory =
                                                response.getconfigure!![i].Value == "1"
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "isLogShareinLogin",
                                            ignoreCase = true
                                        )
                                    ) {
                                        if (response.getconfigure!![i].Value == "1") {
                                            AppUtils.saveSharedPreferenceslogShareinLogin(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferenceslogShareinLogin(
                                                mContext,
                                                false
                                            )
                                        }

                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsCompetitorenable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.isCompetitorImgEnable =
                                            response.getconfigure!![i].Value == "1"
                                        if (Pref.isCompetitorImgEnable) {
                                            AppUtils.saveSharedPreferencesCompetitorImgEnable(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesCompetitorImgEnable(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsOrderStatusRequired",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.isOrderStatusRequired =
                                            response.getconfigure!![i].Value == "1"
                                        if (Pref.isOrderStatusRequired) {
                                            AppUtils.saveSharedPreferencesOrderStatusRequired(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesOrderStatusRequired(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsCurrentStockEnable",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.isCurrentStockEnable =
                                            response.getconfigure!![i].Value == "1"

                                        if (Pref.isCurrentStockEnable) {
                                            AppUtils.saveSharedPreferencesCurrentStock(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesCurrentStock(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsCurrentStockApplicableforAll",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsCurrentStockApplicableforAll =
                                            response.getconfigure!![i].Value == "1"

                                        if (Pref.IsCurrentStockApplicableforAll) {
                                            AppUtils.saveSharedPreferencesCurrentStockApplicableForAll(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesCurrentStockApplicableForAll(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IscompetitorStockRequired",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IscompetitorStockRequired =
                                            response.getconfigure!![i].Value == "1"

                                        if (Pref.IscompetitorStockRequired) {
                                            AppUtils.saveSharedPreferencesIscompetitorStockRequired(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesIscompetitorStockRequired(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsCompetitorStockforParty",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsCompetitorStockforParty =
                                            response.getconfigure!![i].Value == "1"

                                        if (Pref.IsCompetitorStockforParty) {
                                            AppUtils.saveSharedPreferencesIsCompetitorStockforParty(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesIsCompetitorStockforParty(
                                                mContext,
                                                false
                                            )
                                        }
                                    }
                                    //else if (response.getconfigure!![i].Key.equals("IsFaceDetectionOn", ignoreCase = true)) {
                                    else if (response.getconfigure!![i].Key.equals(
                                            "ShowFaceRegInMenu",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsFaceDetectionOn =
                                            response.getconfigure!![i].Value == "1"
                                        if (Pref.IsFaceDetectionOn) {
                                            AppUtils.saveSharedPreferencesIsFaceDetectionOn(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesIsFaceDetectionOn(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsFaceDetection",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsFaceDetection =
                                            response.getconfigure!![i].Value == "1"
                                        if (Pref.IsFaceDetection) {
                                            AppUtils.saveSharedPreferencesIsFaceDetection(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesIsFaceDetection(
                                                mContext,
                                                false
                                            )
                                        }
                                    } else if (response.getconfigure!![i].Key.equals(
                                            "IsFaceDetectionWithCaptcha",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsFaceDetectionWithCaptcha =
                                            response.getconfigure!![i].Value == "1"

                                        if (Pref.IsFaceDetectionWithCaptcha) {
                                            AppUtils.saveSharedPreferencesIsFaceDetectionWithCaptcha(
                                                mContext,
                                                true
                                            )
                                        } else {
                                            AppUtils.saveSharedPreferencesIsFaceDetectionWithCaptcha(
                                                mContext,
                                                false
                                            )
                                        }
                                    } /*else if (response.getconfigure!![i].Key.equals("IsScreenRecorderEnable", ignoreCase = true)) {
                                        Pref.IsScreenRecorderEnable = response.getconfigure!![i].Value == "1"
                                        if (Pref.IsScreenRecorderEnable) { AppUtils.saveSharedPreferencesIsScreenRecorderEnable(mContext, true)
                                        } else {
                                            AppUtils.saveSharedPreferencesIsScreenRecorderEnable(mContext, false)
                                        }
                                    }*/
                                    //else if (response.getconfigure?.get(i)?.Key.equals("IsFromPortal", ignoreCase = true)) {
                                    else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsDocRepoFromPortal",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsFromPortal = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsFromPortal =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsDocRepShareDownloadAllowed",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsDocRepShareDownloadAllowed =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsDocRepShareDownloadAllowed =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuAddAttendance",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuAddAttendance =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuAddAttendance =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuAttendance",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuAttendance =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuAttendance =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuMIS Report",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuMIS_Report =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuMIS_Report =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuAnyDesk",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuAnyDesk =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuAnyDesk =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuPermission Info",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuPermission_Info =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuPermission_Info =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuScan QR Code",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuScan_QR_Code =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuScan_QR_Code =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuChat",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuChat =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuChat =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuWeather Details",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuWeather_Details =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuWeather_Details =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuHome Location",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuHome_Location =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuHome_Location =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuShare Location",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuShare_Location =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuShare_Location =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuMap View",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuMap_View =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuMap_View =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuReimbursement",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuReimbursement =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuReimbursement =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuOutstanding Details PP/DD",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuOutstanding_Details_PP_DD =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuOutstanding_Details_PP_DD =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuStock Details - PP/DD",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuStock_Details_PP_DD =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuStock_Details_PP_DD =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsLeaveGPSTrack",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsLeaveGPSTrack =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsLeaveGPSTrack =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowActivitiesInTeam",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowActivitiesInTeam =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowActivitiesInTeam =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }

                                    // Added setting Login 12-08-21
                                    else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowPartyOnAppDashboard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowPartyOnAppDashboard =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowPartyOnAppDashboard =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowAttendanceOnAppDashboard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowAttendanceOnAppDashboard =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowAttendanceOnAppDashboard =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowTotalVisitsOnAppDashboard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowTotalVisitsOnAppDashboard =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowTotalVisitsOnAppDashboard =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowVisitDurationOnAppDashboard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowVisitDurationOnAppDashboard =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowVisitDurationOnAppDashboard =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowDayStart",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowDayStart =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowDayStart =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsshowDayStartSelfie",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsshowDayStartSelfie =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsshowDayStartSelfie =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowDayEnd",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowDayEnd = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowDayEnd =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsshowDayEndSelfie",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsshowDayEndSelfie =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsshowDayEndSelfie =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowLeaveInAttendance",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowLeaveInAttendance =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowLeaveInAttendance =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }

                                    //19-08-21
                                    else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMarkDistVisitOnDshbrd",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMarkDistVisitOnDshbrd =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMarkDistVisitOnDshbrd =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsPhotoDeleteShow",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsPhotoDeleteShow =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsPhotoDeleteShow =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "GPSAlert",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.GPSAlert = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.GPSAlert =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "GPSAlertwithSound",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.GPSAlertwithSound =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.GPSAlertwithSound =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }

                                    /*29-10-2021 Team Attendance*/
                                    else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsTeamAttendance",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsTeamAttendance =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsTeamAttendance =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }

                                    /*24-11-2021 ITC face And Distributoraccu*/
                                    else if (response.getconfigure?.get(i)?.Key.equals(
                                            "FaceDetectionAccuracyUpper",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.FaceDetectionAccuracyUpper =
                                            response.getconfigure!![i].Value!!
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.FaceDetectionAccuracyUpper =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                        CustomStatic.FaceDetectionAccuracyUpper =
                                            Pref.FaceDetectionAccuracyUpper
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "FaceDetectionAccuracyLower",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.FaceDetectionAccuracyLower =
                                            response.getconfigure!![i].Value!!
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.FaceDetectionAccuracyLower =
                                                response.getconfigure?.get(i)?.Value!!
                                        }
                                        CustomStatic.FaceDetectionAccuracyLower =
                                            Pref.FaceDetectionAccuracyLower
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "DistributorGPSAccuracy",
                                            ignoreCase = true
                                        )
                                    ) {
                                        try {
                                            Pref.DistributorGPSAccuracy =
                                                response.getconfigure!![i].Value!!
                                            if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                Pref.DistributorGPSAccuracy =
                                                    response.getconfigure?.get(i)?.Value!!
                                            }
                                            if (Pref.DistributorGPSAccuracy.length == 0 || Pref.DistributorGPSAccuracy.equals(
                                                    ""
                                                )
                                            ) {
                                                Pref.DistributorGPSAccuracy = "500"
                                            }
                                            Timber.d("Dashboard DistributorGPSAccuracy (try): " + Pref.DistributorGPSAccuracy)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Pref.DistributorGPSAccuracy = "500"
                                            Timber.d("Dashboard DistributorGPSAccuracy (catch): " + Pref.DistributorGPSAccuracy)
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "BatterySetting",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.BatterySetting =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.BatterySetting =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "PowerSaverSetting",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.PowerSaverSetting =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.PowerSaverSetting =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "Show_App_Logout_Notification",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.Show_App_Logout_Notification =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.Show_App_Logout_Notification =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowTypeInRegistration",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowTypeInRegistration =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowTypeInRegistration =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "FaceRegistrationFrontCamera",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.FaceRegistrationFrontCamera =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.FaceRegistrationFrontCamera =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMenuShops",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMenuShops =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMenuShops =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowMyDetails",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowMyDetails =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMyDetails =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsAttendVisitShowInDashboard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsAttendVisitShowInDashboard =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsAttendVisitShowInDashboard =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsShowManualPhotoRegnInApp",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsShowManualPhotoRegnInApp =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowManualPhotoRegnInApp =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "UpdateUserName",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.UpdateUserName =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.UpdateUserName =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "MarkAttendNotification",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.MarkAttendNotification =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.MarkAttendNotification =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsAllowClickForPhotoRegister",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsAllowClickForPhotoRegister =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsAllowClickForPhotoRegister =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsAllowClickForVisit",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsAllowClickForVisit =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsAllowClickForVisit =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "ShowAutoRevisitInDashboard",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.ShowAutoRevisitInDashboard =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.ShowAutoRevisitInDashboard =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "ShowAutoRevisitInAppMenu",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.ShowAutoRevisitInAppMenu =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.ShowAutoRevisitInAppMenu =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "GeofencingRelaxationinMeter",
                                            ignoreCase = true
                                        )
                                    ) {
                                        try {
                                            Pref.GeofencingRelaxationinMeter =
                                                response.getconfigure!![i].Value!!.toInt()
                                            if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                Pref.GeofencingRelaxationinMeter =
                                                    response.getconfigure!![i].Value!!.toInt()
                                            }
                                        } catch (ex: Exception) {
                                            Pref.GeofencingRelaxationinMeter = 250
                                        }
                                    } else if (response.getconfigure?.get(i)?.Key.equals(
                                            "IsRestrictNearbyGeofence",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Pref.IsRestrictNearbyGeofence =
                                            response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsRestrictNearbyGeofence =
                                                response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }
                                    else if (response.getconfigure?.get(i)?.Key.equals("IsShowNearByTeam", ignoreCase = true))
                                    {
                                        Pref.IsShowNearByTeam = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowNearByTeam = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure!![i].Key.equals("AllowProfileUpdate", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.AllowProfileUpdate = response.getconfigure!![i].Value == "1"
                                        }
                                    }else if (response.getconfigure!![i].Key.equals("IsShowRevisitRemarksPopup", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowRevisitRemarksPopup = response.getconfigure!![i].Value == "1"
                                        }
                                    }else if (response.getconfigure!![i].Key.equals("IsAllowShopStatusUpdate", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsAllowShopStatusUpdate = response.getconfigure!![i].Value == "1"
                                        }
                                    }else if (response.getconfigure!![i].Key.equals("ShowTotalVisitAppMenu", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.ShowTotalVisitAppMenu = response.getconfigure!![i].Value == "1"
                                        }
                                    }
                                    else if (response.getconfigure?.get(i)?.Key.equals("OfflineShopAccuracy")) {
                                        try {
                                            Pref.OfflineShopAccuracy = response.getconfigure!![i].Value!!
                                            if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                Pref.OfflineShopAccuracy = response.getconfigure?.get(i)?.Value!!
                                            }
                                            if (Pref.OfflineShopAccuracy.length == 0 || Pref.OfflineShopAccuracy.equals("")) {
                                                Pref.OfflineShopAccuracy = "700"
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Pref.OfflineShopAccuracy = "700"
                                        }
                                    }

                                    else if (response.getconfigure?.get(i)?.Key.equals("AutoRevisitTimeInSeconds")) {
                                        try {
                                            Pref.autoRevisitTimeInSeconds = response.getconfigure!![i].Value!!
                                            if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                Pref.autoRevisitTimeInSeconds = response.getconfigure?.get(i)?.Value!!
                                            }
                                            if (Pref.autoRevisitTimeInSeconds.length == 0 || Pref.autoRevisitTimeInSeconds.equals("")) {
                                                Pref.autoRevisitTimeInSeconds = "600"
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Pref.autoRevisitTimeInSeconds = "600"
                                        }
                                    }else if (response.getconfigure!![i].Key.equals("PartyUpdateAddrMandatory", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.PartyUpdateAddrMandatory = response.getconfigure!![i].Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("LogoutWithLogFile", ignoreCase = true)) {
                                        Pref.LogoutWithLogFile = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.LogoutWithLogFile = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }

                                    else if (response.getconfigure?.get(i)?.Key.equals("IsShowHomeLocationMap", ignoreCase = true)) {
                                        Pref.IsShowHomeLocationMap = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowHomeLocationMap = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }
                                    else if (response.getconfigure?.get(i)?.Key.equals("WillRoomDBShareinLogin", ignoreCase = true)) {
                                        Pref.WillRoomDBShareinLogin = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.WillRoomDBShareinLogin = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("CommonAINotification", ignoreCase = true)) {
                                        Pref.CommonAINotification = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.CommonAINotification = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsFaceRecognitionOnEyeblink", ignoreCase = true)) {
                                        Pref.IsFaceRecognitionOnEyeblink = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsFaceRecognitionOnEyeblink = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                        CustomStatic.IsFaceRecognitionOnEyeblink = Pref.IsFaceRecognitionOnEyeblink
                                    }else if (response.getconfigure?.get(i)?.Key.equals("GPSNetworkIntervalMins", ignoreCase = true)) {
                                        try{
                                            Pref.GPSNetworkIntervalMins =response.getconfigure!![i].Value!!
                                        }catch (e: Exception) {
                                            e.printStackTrace()
                                            Pref.GPSNetworkIntervalMins = "0"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowMarketSpendTimer", ignoreCase = true)) {
                                        Pref.IsShowMarketSpendTimer = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowMarketSpendTimer = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowWorkType", ignoreCase = true)) {
                                        Pref.IsShowWorkType = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowWorkType = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowUploadImageInAppProfile", ignoreCase = true)) {
                                        Pref.IsShowUploadImageInAppProfile = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowUploadImageInAppProfile = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowInactiveCustomer", ignoreCase = true)) {
                                        Pref.IsShowInactiveCustomer = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowInactiveCustomer = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowCalendar", ignoreCase = true)) {
                                        Pref.IsShowCalendar = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowCalendar = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowCalculator", ignoreCase = true)) {
                                        Pref.IsShowCalculator = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowCalculator = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowAttendanceSummary", ignoreCase = true)) {
                                        Pref.IsShowAttendanceSummary = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowAttendanceSummary = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("ShowUserwisePartyWithGeoFence", ignoreCase = true)) {
                                        Pref.ShowUserwisePartyWithGeoFence = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.ShowUserwisePartyWithGeoFence = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("ShowUserwisePartyWithCreateOrder", ignoreCase = true)) {
                                        Pref.ShowUserwisePartyWithCreateOrder = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.ShowUserwisePartyWithCreateOrder = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsRouteUpdateForShopUser", ignoreCase = true)) {
                                        Pref.IsRouteUpdateForShopUser = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsRouteUpdateForShopUser = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsShowUserWiseDateWiseOrderInApp", ignoreCase = true)) {
                                        Pref.IsShowUserWiseDateWiseOrderInApp = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsShowUserWiseDateWiseOrderInApp = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsOrderEditEnable", ignoreCase = true)) {
                                        Pref.IsOrderEditEnable = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsOrderEditEnable = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }else if (response.getconfigure?.get(i)?.Key.equals("IsOrderDeleteEnable", ignoreCase = true)) {
                                        Pref.IsOrderDeleteEnable = response.getconfigure!![i].Value == "1"
                                        if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                            Pref.IsOrderDeleteEnable = response.getconfigure?.get(i)?.Value == "1"
                                        }
                                    }




                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    Log.e(
                        "Dashboard",
                        "willLeaveApprovalEnable================> " + Pref.willLeaveApprovalEnable
                    )

                    progress_wheel.stopSpinning()
                    getConfigFetchApi()

                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    getConfigFetchApi()
                })
        )
    }

    private fun getConfigFetchApi() {
        val repository = ConfigFetchRepoProvider.provideConfigFetchRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        Timber.d("api_call_dash  configFetch()")
        BaseActivity.compositeDisposable.add(
            repository.configFetch()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->

                    val configResponse = result as ConfigFetchResponseModel
                    Timber.d("ConfigFetchApiResponse : " + "\n" + "Status=====> " + configResponse.status + ", Message====> " + configResponse.message)

                    progress_wheel.stopSpinning()
                    if (configResponse.status == NetworkConstant.SUCCESS) {

                        if (!TextUtils.isEmpty(configResponse.min_distance))
                            AppUtils.minDistance = configResponse.min_distance!!

                        if (!TextUtils.isEmpty(configResponse.max_distance))
                            AppUtils.maxDistance = configResponse.max_distance!!

                        if (!TextUtils.isEmpty(configResponse.max_accuracy))
                            AppUtils.maxAccuracy = configResponse.max_accuracy!!

                        if (!TextUtils.isEmpty(configResponse.min_accuracy))
                            AppUtils.minAccuracy = configResponse.min_accuracy!!

                        /*if (!TextUtils.isEmpty(configResponse.idle_time))
                                    AppUtils.idle_time = configResponse.idle_time!!*/

                        if (configResponse.willStockShow != null)
                            Pref.willStockShow = configResponse.willStockShow!!

                        // From Hahnemann
                        if (configResponse.isPrimaryTargetMandatory != null)
                            Pref.isPrimaryTargetMandatory =
                                configResponse.isPrimaryTargetMandatory!!

                        if (configResponse.isRevisitCaptureImage != null)
                            Pref.isRevisitCaptureImage = configResponse.isRevisitCaptureImage!!

                        if (configResponse.isShowAllProduct != null)
                            Pref.isShowAllProduct = configResponse.isShowAllProduct!!

                        if (configResponse.isStockAvailableForAll != null)
                            Pref.isStockAvailableForAll = configResponse.isStockAvailableForAll!!

                        if (configResponse.isStockAvailableForPopup != null)
                            Pref.isStockAvailableForPopup =
                                configResponse.isStockAvailableForPopup!!

                        if (configResponse.isOrderAvailableForPopup != null)
                            Pref.isOrderAvailableForPopup =
                                configResponse.isOrderAvailableForPopup!!

                        if (configResponse.isCollectionAvailableForPopup != null)
                            Pref.isCollectionAvailableForPopup =
                                configResponse.isCollectionAvailableForPopup!!

                        if (configResponse.isDDFieldEnabled != null)
                            Pref.isDDFieldEnabled = configResponse.isDDFieldEnabled!!

                        if (!TextUtils.isEmpty(configResponse.maxFileSize))
                            Pref.maxFileSize = configResponse.maxFileSize!!

                        if (configResponse.willKnowYourStateShow != null)
                            Pref.willKnowYourStateShow = configResponse.willKnowYourStateShow!!

                        if (configResponse.willAttachmentCompulsory != null)
                            Pref.willAttachmentCompulsory =
                                configResponse.willAttachmentCompulsory!!

                        if (configResponse.canAddBillingFromBillingList != null)
                            Pref.canAddBillingFromBillingList =
                                configResponse.canAddBillingFromBillingList!!

                        if (!TextUtils.isEmpty(configResponse.allPlanListHeaderText))
                            Pref.allPlanListHeaderText = configResponse.allPlanListHeaderText!!

                        if (configResponse.willSetYourTodaysTargetVisible != null)
                            Pref.willSetYourTodaysTargetVisible =
                                configResponse.willSetYourTodaysTargetVisible!!

                        if (!TextUtils.isEmpty(configResponse.attendenceAlertHeading))
                            Pref.attendenceAlertHeading = configResponse.attendenceAlertHeading!!

                        if (!TextUtils.isEmpty(configResponse.attendenceAlertText))
                            Pref.attendenceAlertText = configResponse.attendenceAlertText!!

                        if (!TextUtils.isEmpty(configResponse.meetingText))
                            Pref.meetingText = configResponse.meetingText!!

                        if (!TextUtils.isEmpty(configResponse.meetingDistance))
                            Pref.meetingDistance = configResponse.meetingDistance!!

                        if (configResponse.isActivatePJPFeature != null)
                            Pref.isActivatePJPFeature = configResponse.isActivatePJPFeature!!

                        if (configResponse.willReimbursementShow != null)
                            Pref.willReimbursementShow = configResponse.willReimbursementShow!!

                        if (!TextUtils.isEmpty(configResponse.updateBillingText))
                            Pref.updateBillingText = configResponse.updateBillingText!!

                        if (configResponse.isRateOnline != null)
                            Pref.isRateOnline = configResponse.isRateOnline!!

                        if (!TextUtils.isEmpty(configResponse.ppText))
                            Pref.ppText = configResponse.ppText

                        if (!TextUtils.isEmpty(configResponse.ddText))
                            Pref.ddText = configResponse.ddText

                        if (!TextUtils.isEmpty(configResponse.shopText))
                            Pref.shopText = configResponse.shopText

                        if (configResponse.isCustomerFeatureEnable != null)
                            Pref.isCustomerFeatureEnable = configResponse.isCustomerFeatureEnable!!

                        if (configResponse.isAreaVisible != null)
                            Pref.isAreaVisible = configResponse.isAreaVisible!!

                        if (!TextUtils.isEmpty(configResponse.cgstPercentage))
                            Pref.cgstPercentage = configResponse.cgstPercentage

                        if (!TextUtils.isEmpty(configResponse.sgstPercentage))
                            Pref.sgstPercentage = configResponse.sgstPercentage

                        if (!TextUtils.isEmpty(configResponse.tcsPercentage))
                            Pref.tcsPercentage = configResponse.tcsPercentage

                        if (!TextUtils.isEmpty(configResponse.docAttachmentNo))
                            Pref.docAttachmentNo = configResponse.docAttachmentNo

                        if (!TextUtils.isEmpty(configResponse.chatBotMsg))
                            Pref.chatBotMsg = configResponse.chatBotMsg

                        if (!TextUtils.isEmpty(configResponse.contactMail))
                            Pref.contactMail = configResponse.contactMail

                        if (configResponse.isVoiceEnabledForAttendanceSubmit != null)
                            Pref.isVoiceEnabledForAttendanceSubmit =
                                configResponse.isVoiceEnabledForAttendanceSubmit!!

                        if (configResponse.isVoiceEnabledForOrderSaved != null)
                            Pref.isVoiceEnabledForOrderSaved =
                                configResponse.isVoiceEnabledForOrderSaved!!

                        if (configResponse.isVoiceEnabledForInvoiceSaved != null)
                            Pref.isVoiceEnabledForInvoiceSaved =
                                configResponse.isVoiceEnabledForInvoiceSaved!!

                        if (configResponse.isVoiceEnabledForCollectionSaved != null)
                            Pref.isVoiceEnabledForCollectionSaved =
                                configResponse.isVoiceEnabledForCollectionSaved!!

                        if (configResponse.isVoiceEnabledForHelpAndTipsInBot != null)
                            Pref.isVoiceEnabledForHelpAndTipsInBot =
                                configResponse.isVoiceEnabledForHelpAndTipsInBot!!

                        if (configResponse.GPSAlert != null)
                            Pref.GPSAlertGlobal = configResponse.GPSAlert!!

                        if (configResponse.BatterySetting != null)
                            Pref.BatterySettingGlobal = configResponse.BatterySetting!!

                        if (configResponse.PowerSaverSetting != null)
                            Pref.PowerSaverSettingGlobal = configResponse.PowerSaverSetting!!

                        if (configResponse.Show_App_Logout_Notification != null)
                            Pref.Show_App_Logout_Notification_Global =
                                configResponse.Show_App_Logout_Notification!!

                        if (configResponse.FaceRegistrationFrontCamera != null)
                            Pref.FaceRegistrationOpenFrontCamera =
                                configResponse.FaceRegistrationFrontCamera!!

                        if (configResponse.IsShowMyDetails != null)
                            Pref.IsShowMyDetailsGlobal = configResponse.IsShowMyDetails!!

                        if (configResponse.IsAttendVisitShowInDashboard != null)
                            Pref.IsAttendVisitShowInDashboardGlobal =
                                configResponse.IsAttendVisitShowInDashboard!!

                        if (configResponse.IsShowInPortalManualPhotoRegn != null)
                            Pref.IsShowInPortalManualPhotoRegn =
                                configResponse.IsShowInPortalManualPhotoRegn!!

                        if (configResponse.IsShowHomeLocationMap != null)
                            Pref.IsShowHomeLocationMapGlobal = configResponse.IsShowHomeLocationMap!!

                        if (configResponse.ShowApproxDistanceInNearbyShopList != null)
                            Pref.ShowApproxDistanceInNearbyShopList = configResponse.ShowApproxDistanceInNearbyShopList!!

                        if (configResponse.IsnewleadtypeforRuby != null)
                            Pref.IsnewleadtypeforRuby = configResponse.IsnewleadtypeforRuby!!

                        if (configResponse.IsUpdateVisitDataInTodayTable != null)
                            Pref.IsUpdateVisitDataInTodayTable = configResponse.IsUpdateVisitDataInTodayTable!!

                        if (configResponse.ConsiderInactiveShopWhileLogin != null)
                            Pref.ConsiderInactiveShopWhileLogin = configResponse.ConsiderInactiveShopWhileLogin!!

                        //begin mantis id 0027279 ShowPartyWithGeoFence functionality Puja 01-03-2024
                        if (configResponse.ShowPartyWithGeoFence != null)
                            Pref.ShowPartyWithGeoFence = configResponse.ShowPartyWithGeoFence!!

                        //end mantis id 0027279 ShowPartyWithGeoFence functionality Puja 01-03-2024

                        //begin mantis id 0027285 ShowPartyWithCreateOrder functionality Puja 01-03-2024
                        if (configResponse.ShowPartyWithCreateOrder != null)
                            Pref.ShowPartyWithCreateOrder = configResponse.ShowPartyWithCreateOrder!!
                        //end mantis id 0027285 ShowPartyWithCreateOrder functionality Puja 01-03-2024

                        if (configResponse.IsShowPrivacyPolicyInMenu != null)
                            Pref.IsShowPrivacyPolicyInMenu = configResponse.IsShowPrivacyPolicyInMenu!!

                        if (configResponse.IsAllowZeroRateOrder != null)
                            Pref.IsAllowZeroRateOrder = configResponse.IsAllowZeroRateOrder!!

                        if (configResponse.IsViewMRPInOrder != null)
                            Pref.IsViewMRPInOrder = configResponse.IsViewMRPInOrder!!
                    }
                    BaseActivity.isApiInitiated = false
                    checkToCallAlarmConfigApi()

                }, { error ->
                    BaseActivity.isApiInitiated = false
                    error.printStackTrace()
                    checkToCallAlarmConfigApi()
                    progress_wheel.stopSpinning()
                    Timber.d("ConfigFetchApiResponse ERROR: " + error.localizedMessage)
                })
        )
    }

    private fun checkToCallAlarmConfigApi() {
        if (Pref.willAlarmTrigger)
            callAlarmConfig()
        else
            getPjpListApi()

    }

    private fun callAlarmConfig() {
            val repository = AlarmConfigRepoProvider.provideAlarmConfigRepository()
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel
            Timber.d("api_call_dash  alarmConfig()")
            progress_wheel?.spin()
            BaseActivity.compositeDisposable.add(
                repository.alarmConfig()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->

                        val configResponse = result as AlarmConfigResponseModel
                        Timber.d("AlarmConfigApiResponse : " + "\n" + "Status=====> " + configResponse.status + ", Message====> " + configResponse.message)

                        progress_wheel.stopSpinning()
                        if (configResponse.status == NetworkConstant.SUCCESS) {

                            val alarmArr = java.util.ArrayList<AlarmData>()
                            for (item in configResponse.alarm_settings_list!!) {

                                if (AppUtils.getCurrentTimeInMintes() < ((item.alarm_time_hours!!.toInt() * 60) + item.alarm_time_mins!!.toInt())) {
                                    val al = AlarmData()
                                    al.requestCode = 2010 + item.id!!.toInt()
                                    al.id = item.id
                                    al.report_id = item.report_id!!
                                    al.report_title = item.report_title!!
                                    al.alarm_time_hours = item.alarm_time_hours!!
                                    al.alarm_time_mins = item.alarm_time_mins!!

                                    alarmArr.add(al)

                                    AlarmReceiver.setAlarm(
                                        mContext,
                                        item.alarm_time_hours!!.toInt(),
                                        item.alarm_time_mins!!.toInt(),
                                        al.requestCode
                                    )
                                }

                            }

                            AlarmReceiver.saveSharedPreferencesLogList(mContext, alarmArr)
                        }

                        BaseActivity.isApiInitiated = false
                        getPjpListApi()
                    }, { error ->
                        BaseActivity.isApiInitiated = false
                        error.printStackTrace()
                        progress_wheel.stopSpinning()
                        Timber.d("AlarmConfigApiResponse ERROR: " + error.localizedMessage)
                        getPjpListApi()
                    })
            )


    }

    private fun getPjpListApi() {
        if(Pref.isActivatePJPFeature){
            Timber.d("PJP api DashF PJPDetails/PJPList call")
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel
            Timber.d("api_call_dash  getUserPJPList()")
            progress_wheel?.spin()
            val repository = TeamRepoProvider.teamRepoProvider()
            BaseActivity.compositeDisposable.add(
                repository.getUserPJPList(AppUtils.getCurrentDateForShopActi())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as UserPjpResponseModel
                        Timber.d("GET USER PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                        if (response.status == NetworkConstant.SUCCESS) {

                            if (response.pjp_list != null && response.pjp_list.isNotEmpty()) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()

                                    response.pjp_list.forEach {
                                        val pjpEntity = PjpListEntity()
                                        AppDatabase.getDBInstance()?.pjpListDao()
                                            ?.insert(pjpEntity.apply {
                                                pjp_id = it.id
                                                from_time = it.from_time
                                                to_time = it.to_time
                                                customer_name = it.customer_name
                                                customer_id = it.customer_id
                                                location = it.location
                                                date = it.date
                                                remarks = it.remarks
                                            })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getTeamAreaListApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getTeamAreaListApi()
                            }


                        } else {
                            progress_wheel.stopSpinning()
                            getTeamAreaListApi()
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                        error.printStackTrace()
                        getTeamAreaListApi()
                    })
            )
        }else{
            getTeamAreaListApi()
        }

    }

    private fun getTeamAreaListApi() {
        if(Pref.isOfflineTeam){
            val repository = TeamRepoProvider.teamRepoProvider()
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel
            Timber.d("api_call_dash  teamAreaList()")
            progress_wheel?.spin()
            BaseActivity.compositeDisposable.add(
                repository.teamAreaList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as TeamAreaListResponseModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            val list = response.area_list

                            if (list != null && list.isNotEmpty()) {

                                AppDatabase.getDBInstance()?.memberAreaDao()?.deleteAll()

                                doAsync {

                                    list.forEach {
                                        val area = TeamAreaEntity()
                                        AppDatabase.getDBInstance()?.memberAreaDao()
                                            ?.insertAll(area.apply {
                                                area_id = it.area_id
                                                area_name = it.area_name
                                                user_id = it.user_id
                                            })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getTimesheetDropdownApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getTimesheetDropdownApi()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getTimesheetDropdownApi()
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        getTimesheetDropdownApi()
                    })
            )
        }else{
            getTimesheetDropdownApi()
        }

    }

    private fun getTimesheetDropdownApi() {
        if(Pref.willTimesheetShow) {
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel

            Timber.d("api_call_dash  getTimeSheetDropdown()")
            progress_wheel?.spin()
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                repository.getTimeSheetDropdown()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as TimeSheetDropDownResponseModel
                        Timber.d("TIMESHEET DROPDOWN: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                        if (response.status == NetworkConstant.SUCCESS) {

                            AppDatabase.getDBInstance()?.clientDao()?.deleteAll()
                            AppDatabase.getDBInstance()?.projectDao()?.deleteAll()
                            AppDatabase.getDBInstance()?.activityDao()?.deleteAll()
                            AppDatabase.getDBInstance()?.productDao()?.deleteAll()


                            doAsync {

                                response.client_list?.forEach {
                                    val client = ClientListEntity()
                                    AppDatabase.getDBInstance()?.clientDao()?.insertAll(client.apply {
                                        client_id = it.client_id
                                        client_name = it.client_name
                                    })
                                }

                                response.project_list?.forEach {
                                    val project = ProjectListEntity()
                                    AppDatabase.getDBInstance()?.projectDao()?.insertAll(project.apply {
                                        project_id = it.project_id
                                        project_name = it.project_name
                                    })
                                }

                                response.product_list?.forEach {
                                    val product = TimesheetProductListEntity()
                                    AppDatabase.getDBInstance()?.productDao()?.insertAll(product.apply {
                                        product_id = it.product_id
                                        product_name = it.product_name
                                    })
                                }

                                response.activity_list?.forEach {
                                    val activity = ActivityListEntity()
                                    AppDatabase.getDBInstance()?.activityDao()
                                        ?.insertAll(activity.apply {
                                            activity_id = it.activity_id
                                            activity_name = it.activity_name
                                        })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    getTimesheetConfig()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getTimesheetConfig()
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("TIMESHEET DROPDOWN: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                        error.printStackTrace()
                        getTimesheetConfig()
                    })
            )
        }else{
            getTimesheetConfig()
        }

    }

    private fun getTimesheetConfig() {
        if(Pref.willTimesheetShow){
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel

            Timber.d("api_call_dash  timeSheetConfig()")
            progress_wheel?.spin()
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                repository.timeSheetConfig(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as TimeSheetConfigResponseModel
                        Timber.d("TIMESHEET CONFIG: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                        progress_wheel.stopSpinning()

                        if (response.status == NetworkConstant.SUCCESS) {
                            response.apply {
                                Pref.timesheet_past_days = timesheet_past_days
                                Pref.supervisor_name = supervisor_name
                                Pref.client_text = client_text
                                Pref.product_text = product_text
                                Pref.activity_text = activity_text
                                Pref.project_text = project_text
                                Pref.time_text = time_text
                                Pref.comment_text = comment_text
                                Pref.submit_text = submit_text
                            }
                        }

                        gePrimaryAppListApi()

                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("TIMESHEET CONFIG: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                        error.printStackTrace()
                        gePrimaryAppListApi()
                    })
            )
        }else{
            gePrimaryAppListApi()
        }

    }

    private fun gePrimaryAppListApi() {
        if(Pref.isCustomerFeatureEnable){
            progress_wheel.spin()
            Timber.d("api_call_dash  getPrimaryAppList()")
            val repository = ShopListRepositoryProvider.provideShopListRepository()
            BaseActivity.compositeDisposable.add(
                repository.getPrimaryAppList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as PrimaryAppListResponseModel
                        Timber.d("GET PRIMARY APP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                        if (response.status == NetworkConstant.SUCCESS) {

                            if (response.primary_application_list != null && response.primary_application_list!!.isNotEmpty()) {

                                AppDatabase.getDBInstance()?.primaryAppListDao()?.deleteAll()

                                doAsync {

                                    response.primary_application_list?.forEach {
                                        val primaryEntity = PrimaryAppEntity()
                                        AppDatabase.getDBInstance()?.primaryAppListDao()
                                            ?.insertAll(primaryEntity.apply {
                                                primary_app_id = it.id
                                                primary_app_name = it.name
                                            })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        geSecondaryAppListApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                geSecondaryAppListApi()
                            }


                        } else {
                            progress_wheel.stopSpinning()
                            geSecondaryAppListApi()
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("GET PRIMARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                        error.printStackTrace()
                        geSecondaryAppListApi()
                    })
            )
        }else{
            // Revision 1.0 DashboardFragment  Suman App V4.4.6  09-04-2024  mantis id 27357: stage list api call update on refresh begin
            //getEntityTypeListApi()
            geStageApi()
            // Revision 1.0 DashboardFragment  Suman App V4.4.6  09-04-2024  mantis id 27357: stage list api call update on refresh end
        }

    }

    private fun geSecondaryAppListApi() {
        progress_wheel.spin()
        Timber.d("api_call_dash  getSecondaryAppList()")
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
            repository.getSecondaryAppList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as SecondaryAppListResponseModel
                    Timber.d("GET SECONDARY APP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.secondary_application_list != null && response.secondary_application_list!!.isNotEmpty()) {

                            AppDatabase.getDBInstance()?.secondaryAppListDao()?.deleteAll()

                            doAsync {

                                response.secondary_application_list?.forEach {
                                    val secondaryEntity = SecondaryAppEntity()
                                    AppDatabase.getDBInstance()?.secondaryAppListDao()
                                        ?.insertAll(secondaryEntity.apply {
                                            secondary_app_id = it.id
                                            secondary_app_name = it.name
                                        })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    geLeadApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            geLeadApi()
                        }


                    } else {
                        progress_wheel.stopSpinning()
                        geLeadApi()
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    Timber.d("GET SECONDARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                    error.printStackTrace()
                    geLeadApi()
                })
        )
    }

    private fun geLeadApi() {
        progress_wheel.spin()
        Timber.d("api_call_dash  getLeadTypeList()")
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
            repository.getLeadTypeList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as LeadListResponseModel
                    Timber.d("GET LEAD TYPE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.lead_type_list != null && response.lead_type_list!!.isNotEmpty()) {

                            AppDatabase.getDBInstance()?.leadTypeDao()?.deleteAll()

                            doAsync {

                                response.lead_type_list?.forEach {
                                    val leadEntity = LeadTypeEntity()
                                    AppDatabase.getDBInstance()?.leadTypeDao()
                                        ?.insertAll(leadEntity.apply {
                                            lead_id = it.id
                                            lead_name = it.name
                                        })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    geStageApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            geStageApi()
                        }


                    } else {
                        progress_wheel.stopSpinning()
                        geStageApi()
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    Timber.d("GET LEAD TYPE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                    error.printStackTrace()
                    geStageApi()
                })
        )
    }

    private fun geStageApi() {
        progress_wheel.spin()
        Timber.d("api_call_dash  getStagList()")
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
            repository.getStagList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as StageListResponseModel
                    Timber.d("GET STAGE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.stage_list != null && response.stage_list!!.isNotEmpty()) {

                            AppDatabase.getDBInstance()?.stageDao()?.deleteAll()

                            doAsync {

                                response.stage_list?.forEach {
                                    val stageEntity = StageEntity()
                                    AppDatabase.getDBInstance()?.stageDao()
                                        ?.insertAll(stageEntity.apply {
                                            stage_id = it.id
                                            stage_name = it.name
                                        })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    geFunnelStageApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            geFunnelStageApi()
                        }


                    } else {
                        progress_wheel.stopSpinning()
                        geFunnelStageApi()
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    Timber.d("GET STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                    error.printStackTrace()
                    geFunnelStageApi()
                })
        )
    }

    private fun geFunnelStageApi() {
        progress_wheel.spin()
        Timber.d("api_call_dash  getFunnelStageList()")
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
            repository.getFunnelStageList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as FunnelStageListResponseModel
                    Timber.d("GET FUNNEL STAGE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                    if (response.status == NetworkConstant.SUCCESS) {

                        if (response.funnel_stage_list != null && response.funnel_stage_list!!.isNotEmpty()) {

                            AppDatabase.getDBInstance()?.funnelStageDao()?.deleteAll()

                            doAsync {

                                response.funnel_stage_list?.forEach {
                                    val funnelStageEntity = FunnelStageEntity()
                                    AppDatabase.getDBInstance()?.funnelStageDao()
                                        ?.insertAll(funnelStageEntity.apply {
                                            funnel_stage_id = it.id
                                            funnel_stage_name = it.name
                                        })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    getEntityTypeListApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getEntityTypeListApi()
                        }


                    } else {
                        progress_wheel.stopSpinning()
                        getEntityTypeListApi()
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    Timber.d("GET FUNNEL STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                    error.printStackTrace()
                    getEntityTypeListApi()
                })
        )
    }

    private fun getEntityTypeListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        Timber.d("api_call_dash  entityList()")
        println("tag_entityc_call getEntityTypeListApi calling")
        Timber.d("tag_entityc_call getEntityTypeListApi calling")
        BaseActivity.compositeDisposable.add(
            repository.entityList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as EntityResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val list = response.entity_type

                        if (list != null && list.isNotEmpty()) {
                            AppDatabase.getDBInstance()?.entityDao()?.delete()
                            doAsync {
                                list.forEach {
                                    val entity = EntityTypeEntity()
                                    AppDatabase.getDBInstance()?.entityDao()?.insert(entity.apply {
                                        entity_id = it.id
                                        name = it.name
                                    })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    getPartyStatusListApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getPartyStatusListApi()
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        getPartyStatusListApi()
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    getPartyStatusListApi()
                })
        )
    }

    private fun getPartyStatusListApi() {
        if(Pref.willShowPartyStatus){
            val repository = TypeListRepoProvider.provideTypeListRepository()
            progress_wheel.spin()
            Timber.d("api_call_dash  partyStatusList()")
            BaseActivity.compositeDisposable.add(
                repository.partyStatusList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as PartyStatusResponseModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            val list = response.party_status
                            if (list != null && list.isNotEmpty()) {
                                AppDatabase.getDBInstance()?.partyStatusDao()?.delete()
                                doAsync {
                                    list.forEach {
                                        val party = PartyStatusEntity()
                                        AppDatabase.getDBInstance()?.partyStatusDao()
                                            ?.insert(party.apply {
                                                party_status_id = it.id
                                                name = it.name
                                            })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getMeetingTypeListApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getMeetingTypeListApi()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getMeetingTypeListApi()
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        getMeetingTypeListApi()
                    })
            )
        }else{
            getMeetingTypeListApi()
        }

    }

    private fun getMeetingTypeListApi() {
        if(Pref.isMeetingAvailable){
            val repository = LoginRepositoryProvider.provideLoginRepository()
            progress_wheel.spin()
            Timber.d("api_call_dash  getMeetingList()")
            BaseActivity.compositeDisposable.add(
                repository.getMeetingList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as MeetingListResponseModel
                        BaseActivity.isApiInitiated = false
                        if (response.status == NetworkConstant.SUCCESS) {

                            if (response.meeting_type_list != null && response.meeting_type_list!!.size > 0) {

                                AppDatabase.getDBInstance()!!.addMeetingTypeDao().deleteAll()

                                doAsync {
                                    val list = response.meeting_type_list

                                    for (i in list!!.indices) {
                                        val meetingType = MeetingTypeEntity()
                                        meetingType.typeId = list[i].type_id.toInt()
                                        meetingType.typeText = list[i].type_text

                                        AppDatabase.getDBInstance()!!.addMeetingTypeDao()
                                            .insertAll(meetingType)
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getRemarksList()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getRemarksList()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getRemarksList()
                        }

                    }, { error ->
                        error.printStackTrace()
                        BaseActivity.isApiInitiated = false
                        progress_wheel.stopSpinning()
                        getRemarksList()
                    })
            )
        }else{
            getRemarksList()
        }

    }

    private fun getRemarksList() {
        progress_wheel.spin()
        Timber.d("api_call_dash  getRemarksList()")
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
        BaseActivity.compositeDisposable.add(
            repository.getRemarksList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as VisitRemarksResponseModel
                    Timber.d("Visit Remarks List : RESPONSE " + response.status)
                    if (response.status == NetworkConstant.SUCCESS) {
                        AppDatabase.getDBInstance()?.visitRemarksDao()?.delete()

                        doAsync {
                            response.remarks_list?.forEach {
                                val visitRemarks = VisitRemarksEntity()
                                AppDatabase.getDBInstance()?.visitRemarksDao()
                                    ?.insertAll(visitRemarks.apply {
                                        remarks_id = it.id
                                        name = it.name
                                    })
                            }

                            uiThread {
                                progress_wheel.stopSpinning()
                                //changeUI()
                                getNewProductList()
                            }
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        //changeUI()
                        getNewProductList()
                    }
                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    Timber.d("Visit Remarks List : ERROR " + error.localizedMessage)
                    //changeUI()
                    getNewProductList()
                })
        )
    }

    // Revision 2.0   Suman App V4.4.6  04-04-2024  mantis id 27291: New Order Module api implement & room insertion begin

    private fun getNewProductList() {
        if(Pref.ShowPartyWithCreateOrder && Pref.ShowUserwisePartyWithCreateOrder){
            progress_wheel.spin()
            val repository = ProductListRepoProvider.productListProvider()
            BaseActivity.compositeDisposable.add(
                repository.getProductListITC(Pref.session_token!!, Pref.user_id!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as GetProductReq
                        Timber.d("getNewProductList response ${response.status}")
                        if (response.status == NetworkConstant.SUCCESS) {
                            var list = response.product_list
                            if (list != null && list.isNotEmpty()) {
                                doAsync {
                                    AppDatabase.getDBInstance()!!.newProductListDao().deleteAll()
                                    AppDatabase.getDBInstance()?.newProductListDao()?.insertAll(list!!)
                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getNewProductRateList()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getNewProductRateList()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getNewProductRateList()
                        }
                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("getNewProductList error ${error.message}")
                        getNewProductRateList()
                    })
            )
        }else{
            getNewProductRateList()
        }
    }

    private fun getNewProductRateList() {
        if(Pref.ShowPartyWithCreateOrder && Pref.ShowUserwisePartyWithCreateOrder){
            progress_wheel.spin()
            val repository = ProductListRepoProvider.productListProvider()
            BaseActivity.compositeDisposable.add(
                repository.getProductRateListITC(Pref.session_token!!, Pref.user_id!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as GetProductRateReq
                        Timber.d("getNewProductRateList response ${response.status}")
                        if (response.status == NetworkConstant.SUCCESS) {
                            var list = response.product_rate_list
                            if (list != null && list.isNotEmpty()) {
                                doAsync {
                                    Timber.d("rate insert process start ${AppUtils.getCurrentDateTime()}")
                                    AppDatabase.getDBInstance()!!.newRateListDao().deleteAll()
                                    AppDatabase.getDBInstance()?.newRateListDao()?.insertAll(list!!)
                                    Timber.d("rate insert process end ${AppUtils.getCurrentDateTime()}")
                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getOrderHistoryList()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getOrderHistoryList()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            getOrderHistoryList()
                        }
                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("getNewProductRateList error ${error.message}")
                        getOrderHistoryList()
                    })
            )
        }else{
            getOrderHistoryList()
        }
    }

    private fun getOrderHistoryList(){
        var ordHisL = AppDatabase.getDBInstance()!!.newOrderDataDao().getAllOrder() as ArrayList<NewOrderDataEntity>
        if(Pref.ShowPartyWithCreateOrder && ordHisL.size==0 && Pref.ShowUserwisePartyWithCreateOrder){
            Timber.d("getOrderHistoryList call")
            progress_wheel.spin()
            val repository = ProductListRepoProvider.productListProvider()
            BaseActivity.compositeDisposable.add(
                repository.getOrderHistory(Pref.user_id!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as GetOrderHistory
                        Timber.d("getOrderHistoryList response ${response.status}")
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                Timber.d("getOrderHistoryList data save begin ${AppUtils.getCurrentDateTime()}")
                                var order_list = response.order_list
                                for(i in 0..order_list.size-1){
                                    var obj = NewOrderDataEntity()
                                    obj.order_id = order_list.get(i).order_id
                                    obj.order_date = order_list.get(i).order_date
                                    obj.order_time = order_list.get(i).order_time
                                    obj.order_date_time = order_list.get(i).order_date_time
                                    obj.shop_id = order_list.get(i).shop_id
                                    obj.shop_name = order_list.get(i).shop_name
                                    obj.shop_type = order_list.get(i).shop_type
                                    obj.isInrange = order_list.get(i).isInrange
                                    obj.order_lat = order_list.get(i).order_lat
                                    obj.order_long = order_list.get(i).order_long
                                    obj.shop_addr = order_list.get(i).shop_addr
                                    obj.shop_pincode = order_list.get(i).shop_pincode
                                    obj.order_total_amt = order_list.get(i).order_total_amt.toString()
                                    obj.order_remarks = order_list.get(i).order_remarks
                                    obj.isUploaded = true

                                    var objProductL:ArrayList<NewOrderProductEntity> = ArrayList()
                                    for( j in 0..order_list.get(i).product_list.size-1){
                                        var objProduct = NewOrderProductEntity()
                                        objProduct.order_id = order_list.get(i).product_list.get(j).order_id
                                        objProduct.product_id = order_list.get(i).product_list.get(j).product_id
                                        objProduct.product_name = order_list.get(i).product_list.get(j).product_name
                                        objProduct.submitedQty = order_list.get(i).product_list.get(j).submitedQty.toInt().toString()
                                        objProduct.submitedSpecialRate = order_list.get(i).product_list.get(j).submitedSpecialRate.toString()
                                        objProduct.shop_id = order_list.get(i).shop_id
                                        objProductL.add(objProduct)
                                    }

                                    AppDatabase.getDBInstance()!!.newOrderDataDao().insert(obj)
                                    AppDatabase.getDBInstance()!!.newOrderProductDao().insertAll(objProductL)

                                }
                                uiThread {
                                    Timber.d("getOrderHistoryList data save end ${AppUtils.getCurrentDateTime()}")
                                    progress_wheel.stopSpinning()
                                    changeUI()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            changeUI()
                        }
                    }, { error ->
                        progress_wheel.stopSpinning()
                        Timber.d("getOrderHistoryList error ${error.message}")
                        changeUI()
                    })
            )
        }else{
            Timber.d("getOrderHistoryList call bypass")
            changeUI()
        }
    }

// Revision 2.0   Suman App V4.4.6  04-04-2024  mantis id 27291: New Order Module api implement & room insertion end

    @SuppressLint("RestrictedApi")
    private fun changeUI() {

        Timber.d("api_call_dash  changeUI()")
        tv_shop.text = Pref.shopText + "(s)"

        if (Pref.willShowUpdateDayPlan)
            tv_view_all.visibility = View.VISIBLE
        else
            tv_view_all.visibility = View.GONE

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        best_performing_shop_TV.text = getString(R.string.todays_task)

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        if (Pref.isAttendanceFeatureOnly) {
            ll_attendance_report_main.visibility = View.VISIBLE
            rl_dashboard_fragment_main.visibility = View.GONE
        } else {
            ll_attendance_report_main.visibility = View.GONE
            rl_dashboard_fragment_main.visibility = View.VISIBLE
        }

        if (!Pref.isMeetingAvailable && !Pref.isShopAddEditAvailable)
            fab.visibility = View.GONE
        else
            fab.visibility = View.VISIBLE

        if (Pref.isServiceFeatureEnable) {
            tv_order.text = getString(R.string.myjobs)
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.GONE
        } else if (Pref.willActivityShow) {
            tv_order.text = getString(R.string.activities)
            no_of_order_TV.text = getString(R.string.today_activity)
            avgOrder.text = InfoWizard.getActivityForToday()
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.VISIBLE
        } else {
            shop_tome_order_tab_LL.visibility = View.VISIBLE
            if (Pref.isQuotationShow) {
                tv_order.text = getString(R.string.quotation)
                no_of_order_TV.text = getString(R.string.total_quot)
                avgOrder.text =
                    getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
                iv_order_icon.visibility = View.GONE
                iv_quot_icon.visibility = View.VISIBLE
            } else {
                no_of_order_TV.text = getString(R.string.total_order_value_new)
                avgOrder.text =
                    getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
                iv_order_icon.visibility = View.VISIBLE
                iv_quot_icon.visibility = View.GONE

                if (Pref.isOrderReplacedWithTeam) {
                    tv_order.text = getString(R.string.team_details)
                    iv_order_icon.setImageResource(R.drawable.ic_team_icon)
                    //price_RL.visibility = View.GONE
                    ll_dash_total_order_newD.visibility = View.GONE
                } else {
                    tv_order.text = getString(R.string.orders)
                    iv_order_icon.setImageResource(R.drawable.ic_dashboard_order_icon)

                    if (Pref.isOrderShow) {
                        order_ll.visibility = View.VISIBLE
                        //price_RL.visibility = View.VISIBLE
                        //avgOrder.visibility = View.VISIBLE
                        //no_of_order_TV.visibility = View.VISIBLE

                        ll_dash_total_order_newD.visibility = View.VISIBLE
                    } else {
                        order_ll.visibility = View.GONE
                        //price_RL.visibility = View.GONE
                        //avgOrder.visibility = View.GONE
                        //no_of_order_TV.visibility = View.GONE

                        ll_dash_total_order_newD.visibility = View.GONE
                    }
                }
            }
        }

        if (Pref.isChatBotShow)
            fab_bot.visibility = View.VISIBLE
        else
            fab_bot.visibility = View.GONE

        if (Pref.isShowTimeline)
            history_ll.visibility = View.VISIBLE
        else
            history_ll.visibility = View.GONE

        if (Pref.IsShowMarkDistVisitOnDshbrd) {
            //DDVisit_TV.visibility = View.VISIBLE
            //DDVisitdate_TV.visibility = View.VISIBLE
            ll_dash_point_visit_newD.visibility = View.VISIBLE
        } else {
            //DDVisit_TV.visibility = View.GONE
            //DDVisitdate_TV.visibility = View.GONE
            ll_dash_point_visit_newD.visibility = View.GONE
        }

        if (Pref.IsShowDayStart) {
            //StartRL.visibility = View.VISIBLE
            //start_shop.visibility = View.VISIBLE

            ll_dash_day_start_newD.visibility = View.VISIBLE
        } else {
            //StartRL.visibility = View.GONE
            //start_shop.visibility = View.GONE

            ll_dash_day_start_newD.visibility = View.GONE

        }
        if (Pref.IsShowDayEnd) {
            //endRL.visibility = View.VISIBLE
            //enddate_TV.visibility = View.VISIBLE
            ll_dash_day_end_newD.visibility = View.VISIBLE
        } else {
            //endRL.visibility = View.GONE
            //enddate_TV.visibility = View.GONE
            ll_dash_day_end_newD.visibility = View.GONE
        }
        if(Pref.ShowPartyWithCreateOrder && Pref.ShowUserwisePartyWithCreateOrder){
            ll_dash_total_mew_order_newD.visibility = View.VISIBLE
            order_ll_new.visibility = View.VISIBLE
        }else{
            ll_dash_total_mew_order_newD.visibility = View.GONE
            order_ll_new.visibility = View.GONE
        }

        if (Pref.IsShowTotalVisitsOnAppDashboard) {
//            shops_RL.visibility = View.VISIBLE
            //n_shops_TV.visibility = View.VISIBLE
            //no_of_shop_TV.visibility = View.VISIBLE
            ll_dash_total_visit_newD.visibility = View.VISIBLE
        } else {
//            shops_RL.visibility = View.GONE
            //n_shops_TV.visibility = View.GONE
            //no_of_shop_TV.visibility = View.GONE
            ll_dash_total_visit_newD.visibility = View.GONE
        }

        if (Pref.IsAttendVisitShowInDashboardGlobal) {
            if (Pref.IsAttendVisitShowInDashboard) {
                ll_dash_visit_attendance_newD.visibility = View.VISIBLE
            } else {
                ll_dash_visit_attendance_newD.visibility = View.GONE
            }
        } else {
            ll_dash_visit_attendance_newD.visibility = View.GONE
        }


        if (Pref.IsShowVisitDurationOnAppDashboard) {
//            time_RL.visibility = View.VISIBLE
            //n_time_TV.visibility = View.VISIBLE
            //no_of_time_TV.visibility = View.VISIBLE
            ll_dash_visit_duration_newD.visibility = View.VISIBLE
        } else {
//            time_RL.visibility = View.GONE
            //n_time_TV.visibility = View.GONE
            //no_of_time_TV.visibility = View.GONE
            ll_dash_visit_duration_newD.visibility = View.GONE
        }

        if (Pref.IsShowPartyOnAppDashboard)
            shop_ll.visibility = View.VISIBLE
        else
            shop_ll.visibility = View.GONE

        //Pref.IsShowAttendanceOnAppDashboard = true
        if (Pref.IsShowAttendanceOnAppDashboard)
            attandance_ll.visibility = View.VISIBLE
        else
            attandance_ll.visibility = View.GONE

        if (Pref.ShowAutoRevisitInDashboard)
            revisit_ll.visibility = View.VISIBLE
        else
            revisit_ll.visibility = View.GONE


        initAdapter()
        initBottomAdapter()

        if(Pref.IsShowWorkType){
            reportList.visibility = View.VISIBLE
        }
        else{
            reportList.visibility = View.GONE
        }
        try {
            timerTV = view!!.findViewById(R.id.tv_dash_frag_timer)
            if(Pref.IsShowMarketSpendTimer){
                timerTV.visibility = View.VISIBLE
                try{
                    if(!Pref.DayStartTime.equals("")){
                        var currentTime = System.currentTimeMillis().toString()
                        var timeDiff = AppUtils.getTimeFromTimeSpan(Pref.DayStartTime,System.currentTimeMillis().toString())
                        timeDiff = timeDiff.split(":").get(0)+":"+timeDiff.split(":").get(1)
                        timerTV.text = "$timeDiff"
                    }
                }catch (ex:Exception){
                    Timber.d("ex timer text")
                }
            }else{
                timerTV.visibility = View.GONE
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }


        (mContext as DashboardActivity).updateUI()
    }


    companion object {
        lateinit var iv_screen_status: ImageView
        lateinit var tv_timer: TextView
        var hbRecorder: HBRecorder? = null
        lateinit var ll_recorder_root: LinearLayout
        var isRecordRootVisible: Boolean = false

        lateinit var timerTV: TextView

    }


/*    fun timerRecord(isStart:Boolean){
        //screen_timer.setBase(SystemClock.elapsedRealtime());
        if(isStart){
            iv_screen_status.setImageResource(R.drawable.green_round)
            screen_timer.start()
        }else{
            iv_screen_status.setImageResource(R.drawable.red_circle)
            screen_timer.stop()
        }

    }

    fun cancelTimer(){
        screen_timer.setBase(SystemClock.elapsedRealtime())
    }*/


    private fun startRecordingScreen() {

        if (hbRecorder == null) {
            hbRecorder = HBRecorder(mContext, this)
        }

        //DashboardFragment.cancelTimer()
        //DashboardFragment.timerRecord(true)

        hbRecorder!!.enableCustomSettings()
        hbRecorder!!.setOutputFormat("MPEG_4")
        hbRecorder!!.isAudioEnabled(false)
        hbRecorder!!.recordHDVideo(false)
        hbRecorder!!.setVideoFrameRate(20)
        hbRecorder!!.setVideoBitrate(1000000)


        val mediaProjectionManager =
            getActivity()!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(permissionIntent, 271)
    }

    override fun HBRecorderOnError(errorCode: Int, reason: String?) {

    }

    override fun HBRecorderOnStart() {
        iv_screen_status.setImageResource(R.drawable.green_round)
        val intent = Intent(mContext, ScreenRecService::class.java)
        intent.action = CustomConstants.START_Screen_SERVICE
        mContext.startService(intent)
    }


    override fun HBRecorderOnComplete() {
        var intent = Intent(mContext, ScreenRecService::class.java)
        intent.action = CustomConstants.STOP_Screen_SERVICE
        mContext.stopService(intent)

        iv_screen_status.setImageResource(R.drawable.red_circle)

        //DashboardFragment.timerRecord(false)


        val path = hbRecorder!!.filePath
        val fileUrl = Uri.parse(path)
        val file = File(fileUrl.path)
        val uri = Uri.fromFile(file)

        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_simple_screen_rec)
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_simple_ok) as AppCustomTextView
        val heading = simpleDialog.findViewById(R.id.dialog_simple_header_TV) as AppCustomTextView
        heading.text =
            "You may delete the recording file " + hbRecorder!!.fileName + " from storage after sharing it."
        dialogYes.setOnClickListener({ view ->
            simpleDialog.dismiss()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "video/mp4"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            mContext.startActivity(Intent.createChooser(shareIntent, "Share video using"));
        })

        simpleDialog.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 271) {
                /*           val intent = Intent(mContext, ScreenRecService::class.java)
                           intent.action = CustomConstants.START_Screen_SERVICE
                           mContext.startService(intent)*/
                hbRecorder!!.startScreenRecording(data, resultCode, mContext as Activity)
            }
            if (requestCode == 171) {
                progress_wheel.stopSpinning()
                println("reg_face - dashboard_frag face Detect Face Match" + AppUtils.getCurrentDateTime());
                Timber.d("onActivityResult ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
                if (Pref.isCalledFromStart) {
                    Pref.isStartCall = true
                    Timber.d("onActivityResult if ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
                    start_TV.performClick()
                } else {
                    Pref.isEndCall = true
                    Timber.d("onActivityResult else ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
                    end_TV.performClick()
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event!!.getActionMasked()) {
            MotionEvent.ACTION_DOWN -> {
                dX = v!!.getX() - event.getRawX();
                dY = v!!.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
            }
            MotionEvent.ACTION_MOVE -> {
                v!!.setY(event.getRawY() + dY);
                v!!.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
            }
            MotionEvent.ACTION_UP -> {
                /* if (lastAction == MotionEvent.ACTION_DOWN)
                     Toast.makeText(mContext,"cl",Toast.LENGTH_SHORT).show()*/

            }
            else -> {
                return false
            }
        }
        return true;
    }

    ///// added on 13/08/2021

    private var isGetLocation = -1
    private var tagNoNearbyShop: Boolean = false

    //////start
    fun getLocforStart() {
        Timber.d("getLocforStart check11 ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
        if (AppUtils.isOnline(mContext)) {
            if (AppUtils.mLocation != null) {
                if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        Timber.d("=====getLocforStart getNearyShopList call=====")
                        getNearyShopList(AppUtils.mLocation!!)
                    } else {
                        //getDDList(AppUtils.mLocation!!)
                        Timber.d("=====getLocforStart singleLocation call=====")
                        singleLocation()
                    }
                } else {
                    Timber.d("=====Inaccurate current location (Local Shop List)=====")
                    singleLocation()
                }
            } else {
                Timber.d("=====null location (Local Shop List)======")
                singleLocation()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

    }

    private fun singleLocation() {

        Timber.d("singleLocation call" + " , " + " Time :" + AppUtils.getCurrentDateTime())

        progress_wheel.spin()
        isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
            object : SingleShotLocationProvider.LocationCallback {
                override fun onStatusChanged(status: String) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onProviderEnabled(status: String) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onProviderDisabled(status: String) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onNewLocationAvailable(location: Location) {

                    Timber.d("singleLocation call onNewLocationAvailable" + " , " + " Time :" + AppUtils.getCurrentDateTime())
                    if (isGetLocation == -1) {
                        isGetLocation = 0
                        if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                            (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                            progress_wheel.stopSpinning()
                        } else{
                            Timber.d("singleLocation call getNearyShopList" + " , " + " Time :" + AppUtils.getCurrentDateTime())
                            getNearyShopList(location)
                        }
                    }
                }
            })
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(
                            "GPS data to show nearby party is inaccurate. Please stop " +
                                    "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list."
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

    fun getNearyShopList(location: Location) {


        Timber.d("getNearyShopList DAYSTART" + " , " + " Time :" + AppUtils.getCurrentDateTime())

        try{
            var nearestDist = 5000
            var nearBy: Double = 500.00
            try {
                nearBy =  Pref.DistributorGPSAccuracy.toDouble()
            } catch (e: Exception) {
                Pref.DistributorGPSAccuracy = "500"
                e.printStackTrace()
            }
            var shop_id: String = ""
            var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
            var finalNearByDD: AssignToDDEntity = AssignToDDEntity()
            val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
            val newList = java.util.ArrayList<AddShopDBModelEntity>()
            for (i in allShopList.indices) {
                newList.add(allShopList[i])
            }
            Timber.d("getNearyShopList newList ${newList.size}" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "nearby dd found")
            if (newList != null && newList.size > 0) {
                for (i in 0 until newList.size) {
                    val shopLat: Double = newList[i].shopLat
                    val shopLong: Double = newList[i].shopLong
                    if (shopLat != null && shopLong != null) {
                        val shopLocation = Location("")
                        shopLocation.latitude = shopLat
                        shopLocation.longitude = shopLong
                        //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                        val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                            location,
                            shopLocation,
                            Pref.DistributorGPSAccuracy.toInt()
                        )
                        var dist = location.distanceTo(shopLocation).toInt()  //21-10-2021
                        if (isShopNearby) {
                            if ((location.distanceTo(shopLocation)) < nearBy) {
                                nearBy = location.distanceTo(shopLocation).toDouble()
                                finalNearByShop = newList[i]
                            }
                            //startDay(newList[i], location)
                            //break
                        } else {
                            if (dist < nearestDist) {
                                nearestDist = dist
                            }
                        }
                    }
                }

            }
            else {

            }
            val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
            val newDDList = java.util.ArrayList<AssignToDDEntity>()
            for (i in allDDList.indices) {
                newDDList.add(allDDList[i])
            }
            Timber.d("getNearyShopList newDDList ${newDDList.size}" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "nearby dd found")
            if (newDDList != null && newDDList.size > 0) {
                for (i in 0 until newDDList.size) {
                    val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                    val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                    if (ddLat != null && ddLong != null) {
                        val ddLocation = Location("")
                        ddLocation.latitude = ddLat
                        ddLocation.longitude = ddLong
                        //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                        var isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                            location,
                            ddLocation,
                            Pref.DistributorGPSAccuracy.toInt()
                        )
                        var dist = location.distanceTo(ddLocation).toInt()  //21-10-2021
                        if (isShopNearby) {
                            if ((location.distanceTo(ddLocation)) < nearBy) {
                                nearBy = location.distanceTo(ddLocation).toDouble()
                                finalNearByDD = newDDList[i]
                            }
                            //startDay(newList[i], location)
                            //break
                        } else {
                            if (dist < nearestDist) {
                                nearestDist = dist
                            }
                        }
                    }
                }

            }
            else {

            }

            if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
                Timber.d("DAYSTART" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "nearby dd found")
                startDay(finalNearByShop, finalNearByDD, location, false)
            }
            else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
                Timber.d("DAYSTARTT" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "nearby shop found")
                startDay(finalNearByShop, finalNearByDD, location, true)
            }
            else {
                Timber.d("DAYSTARTTTT" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "no nearby shop/dd " + "user lat: " + location.latitude.toString() + " long :" + location.longitude)

                progress_wheel.stopSpinning()
                // 27-08-21 For ITC
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_message)
                val dialogHeader =
                    simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV =
                    simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                if (nearestDist == 5000) {
                    dialogHeader.text =
                        "No nearby Shop/Point found..." + ". Current location has been detected " + nearestDist.toString() + " mtr or more distance from the Distributor or Retail point from your handset GPS."
                } else {
                    dialogHeader.text =
                        "No nearby Shop/Point found..." + ". Current location has been detected " + nearestDist.toString() + " mtr distance from the Distributor or Retail point from your handset GPS."
                }

                val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
            }
        }catch (ex:Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            Timber.d("getNearyShopList ex ${ex.message}" + " , " + " Time :" + AppUtils.getCurrentDateTime())
        }

    }

    fun startDay(
        nearByShop: AddShopDBModelEntity,
        nearByDD: AssignToDDEntity,
        loc: Location,
        isShop: Boolean
    ) {
        try {
            var dayst: DaystartDayendRequest = DaystartDayendRequest()
            dayst.user_id = Pref.user_id
            dayst.session_token = Pref.session_token
            //dayst.date = AppUtils.getCurrentDateTime()
            dayst.date = AppUtils.getCurrentDateTimeNew()
            try{
                Timber.d("DashFrag : startDay :  getCurrentDateTime() " + AppUtils.getCurrentDateTime() + " getCurrentDateTimeNew() "+  AppUtils.getCurrentDateTimeNew())
            }catch (ex:Exception){
                ex.printStackTrace()
                Timber.d("DashFrag : startDay :  err ${ex.message} ")
            }
            if(dayst.date.equals("") || dayst.date==null){
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                return
            }


            dayst.location_name = LocationWizard.getNewLocationName(mContext, loc.latitude, loc.longitude)
            dayst.latitude = loc.latitude.toString()
            dayst.longitude = loc.longitude.toString()
            dayst.IsDDvistedOnceByDay = "0"
            dayst.visit_distributor_date_time = ""
            dayst.visit_distributor_id = ""
            dayst.visit_distributor_name = ""
            if (isShop) {
                //dayst.latitude = nearByShop.shopLat.toString()
                //dayst.longitude = nearByShop.shopLong.toString()
                dayst.shop_type = nearByShop.type
                dayst.shop_id = nearByShop.shop_id
                dayst.isStart = "1"
                dayst.isEnd = "0"
                dayst.sale_Value = "0.0"
                dayst.remarks = ""
            } else {
                //dayst.latitude = nearByDD.dd_latitude.toString()
                //dayst.longitude = nearByDD.dd_longitude.toString()
                dayst.shop_type = "4"
                dayst.shop_id = nearByDD.dd_id
                dayst.isStart = "1"
                dayst.isEnd = "0"
                dayst.sale_Value = "0.0"
                dayst.remarks = ""
            }

            var addr = ""
            try {
                addr = LocationWizard.getAdressFromLatlng(mContext, loc.latitude, loc.longitude)
            } catch (ex: Exception) {
                addr = ""
            }

            dayst.attendance_worktype_id = Pref.AttendWorkTypeID
            dayst.attendance_worktype_name = Pref.AttendWorkTypeName

            progress_wheel.spin()
            val repository = DayStartEndRepoProvider.dayStartRepositiry()
            BaseActivity.compositeDisposable.add(
                repository.dayStart(dayst)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        Timber.d(
                            "DayStart (DashboardFrag): DayStarted Success status " + result.status + " lat " +
                                    loc.latitude.toString() + " long " + loc.longitude.toString() + " addr " + addr + " " + AppUtils.getCurrentDateTime()
                        )
                        val response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            //(mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                            //voiceAttendanceMsg("Hi, your day start marked successfully.")
                            isStartOrEndDay(false)
                        }
                    }, { error ->
                        if (error == null) {
                            Timber.d("DayStart (DashboardFrag) : ERROR " + "UNEXPECTED ERROR IN DayStart API " + AppUtils.getCurrentDateTime())
                        } else {
                            Timber.d("DayStart (DashboardFrag) : ERROR " + error.localizedMessage + " " + AppUtils.getCurrentDateTime())
                            error.printStackTrace()
                        }
                        progress_wheel.stopSpinning()
                    })
            )

        } catch (ex: Exception) {
            ex.printStackTrace()
            progress_wheel.stopSpinning()
        }

    }


    //////end
    fun getLocforEnd() {
        if (AppUtils.isOnline(mContext)) {
            if (AppUtils.mLocation != null) {
                if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        getNearyShopListEnd(AppUtils.mLocation!!)
                    } else {
                        //getDDList(AppUtils.mLocation!!)
                        singleLocationEnd()
                    }
                } else {
                    Timber.d("=====Inaccurate current location (Local Shop List)=====")
                    singleLocationEnd()
                }
            } else {
                Timber.d("=====null location (Local Shop List)======")
                singleLocationEnd()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

    }

    private fun singleLocationEnd() {
        progress_wheel.spin()
        isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
            object : SingleShotLocationProvider.LocationCallback {
                override fun onStatusChanged(status: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onProviderEnabled(status: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onProviderDisabled(status: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onNewLocationAvailable(location: Location) {
                    if (isGetLocation == -1) {
                        isGetLocation = 0
                        if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                            (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                            progress_wheel.stopSpinning()
                        } else
                            getNearyShopListEnd(location)
                    }
                }

            })

        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(
                            "GPS data to show nearby party is inaccurate. Please stop " +
                                    "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list."
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

    fun getNearyShopListEnd(location: Location) {
        var nearestDist = 5000
        //var nearBy: Double = Pref.shopLocAccuracy.toDouble()
//        var nearBy: Double = 4000.00
        var nearBy: Double = Pref.DistributorGPSAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                        location,
                        shopLocation,
                        Pref.DistributorGPSAccuracy.toInt()
                    )
                    var dist = location.distanceTo(shopLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    } else {
                        if (dist < nearestDist) {
                            nearestDist = dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                        location,
                        ddLocation,
                        Pref.DistributorGPSAccuracy.toInt()
                    )
                    var dist = location.distanceTo(ddLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    } else {
                        if (dist < nearestDist) {
                            nearestDist = dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }


        //finalNearByDD=newDDList[5]

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            Timber.d("DAYEND" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "nearby dd found")
            endDay(finalNearByShop, finalNearByDD, location, false)
            //checkDayStartEndStatus(finalNearByShop, finalNearByDD, location, false)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            Timber.d("DAYEND" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "nearby shop found")
            endDay(finalNearByShop, finalNearByDD, location, true)
            //checkDayStartEndStatus(finalNearByShop, finalNearByDD, location, true)
        } else {
            Timber.d("DAYEND" + " , " + " Time :" + AppUtils.getCurrentDateTime() + "no nearby shop/dd " + "user lat: " + location.latitude.toString() + " long :" + location.longitude)
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader =
                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV =
                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
            if (nearestDist == 5000) {
                dialogHeader.text =
                    "No nearby Shop/Point found..." + ". Current location has been detected " + nearestDist.toString() + " mtr or more distance from the Distributor or Retail point from your handset GPS."
            } else {
                dialogHeader.text =
                    "No nearby Shop/Point found..." + ". Current location has been detected " + nearestDist.toString() + " mtr distance from the Distributor or Retail point from your handset GPS."
            }

            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }

    }

    fun checkDayStartEndStatus(nearByShop: AddShopDBModelEntity, nearByDD: AssignToDDEntity, loc: Location, isShop: Boolean) {
        try {
            val repository = DayStartEndRepoProvider.dayStartRepositiry()
            BaseActivity.compositeDisposable.add(
                repository.dayStartEndStatus(AppUtils.getCurrentDateyymmdd())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        Timber.d("Login DayStart : RESPONSE " + result.status + AppUtils.getCurrentDateTime())
                        val response = result as StatusDayStartEnd
                        if (response.status == NetworkConstant.SUCCESS) {
                            Pref.DayStartMarked = response.DayStartMarked!!
                            Pref.DayEndMarked = response.DayEndMarked!!
                            Pref.DayStartShopType = response.day_start_shop_type!!
                            Pref.DayStartShopID = response.day_start_shop_id!!
                            Pref.IsDDvistedOnceByDay = response.IsDDvistedOnceByDay!!
                            if(Pref.DayStartMarked && Pref.IsShowDayStart){
                                endDay(nearByShop, nearByDD, loc, isShop)
                            }
                        }else{
                            Pref.DayStartMarked = false
                            Pref.DayEndMarked = false
                            Toaster.msgShort(mContext,"Day Start not found for ${AppUtils.getCurrentDateyymmdd()}")
                        }
                    }, { error ->
                        if (error == null) {
                            Timber.d("Login DayStart : ERROR " + "UNEXPECTED ERROR IN DayStart API "+ AppUtils.getCurrentDateTime())
                        } else {
                            Timber.d("Login DayStart : ERROR " + error.localizedMessage + " "+ AppUtils.getCurrentDateTime())
                            error.printStackTrace()
                        }
                        Pref.DayStartMarked = false
                        Pref.DayEndMarked = false
                        Toaster.msgShort(mContext,"Day Start not found for ${AppUtils.getCurrentDateyymmdd()}")

                    })
            )
        } catch (ex: java.lang.Exception) {
            Timber.d("Login ex DayStart : ERROR  ${ex.message} " + "UNEXPECTED ERROR IN DayStart API "+ AppUtils.getCurrentDateTime())
            ex.printStackTrace()
            Toaster.msgShort(mContext,"Something went wrong.")
        }
    }

    fun endDay(nearByShop: AddShopDBModelEntity, nearByDD: AssignToDDEntity, loc: Location, isShop: Boolean) {
        progress_wheel.stopSpinning()
        var saleValue: String = ""
        /*if (isShop) {
            if (Pref.DayStartShopType.equals("1") && nearByShop.type.equals("1")) {
                progress_wheel.stopSpinning()
                (mContext as DashboardActivity).showSnackMessage("You must visit office to mark day end! Thanks.")
                return
            }
        }*/

        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_end_day_sale_value)
        val tv_head: AppCustomTextView = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
        val et_saleValue: EditText = simpleDialog.findViewById(R.id.dialog_et_sale_value) as EditText
        val submit = simpleDialog.findViewById(R.id.tv_dialog_submit) as AppCustomTextView

        // Rev 3.0 Suman 03-06-2024 Suman DashboardFragment mantis 27499 begin
        tv_head.text = "Enter Delivery value"
        // Rev 3.0 Suman 03-06-2024 Suman DashboardFragment mantis 27499 end

        et_saleValue.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                et_saleValue.setBackgroundResource(R.drawable.blue_line_custom_selected)
            } else {
                et_saleValue.setBackgroundResource(R.drawable.blue_line_custom)
            }
        })

        /*  try {
              et_saleValue.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 2)))
          } catch (ex: Exception) {
              ex.printStackTrace()
          }*/
        submit.setOnClickListener({ view ->
            simpleDialog.cancel()

            saleValue = et_saleValue.text.toString()

            if (saleValue.length > 0) {
                try {
                    progress_wheel.spin()

                    var dayst: DaystartDayendRequest = DaystartDayendRequest()
                    dayst.user_id = Pref.user_id
                    dayst.session_token = Pref.session_token
                    //dayst.date = AppUtils.getCurrentDateTime()
                    dayst.date = AppUtils.getCurrentDateTimeNew()

                    //new time work
                    //Pref.approvedOutTimeServerFormat="15:21:00"
                    var onlyTime = dayst.date!!.split(" ").get(1)
                    var timeDayend = LocalTime.parse(onlyTime)
                    var timeEndGlobal = LocalTime.parse(Pref.approvedOutTimeServerFormat)
                    if(timeDayend>timeEndGlobal){
                        dayst.date = AppUtils.getCurrentDateNew() + " " + timeEndGlobal
                    }


                    dayst.location_name = LocationWizard.getNewLocationName(mContext, loc.latitude, loc.longitude)
                    dayst.latitude = loc.latitude.toString()
                    dayst.longitude = loc.longitude.toString()
                    if (Pref.IsDDvistedOnceByDay) {
                        dayst.IsDDvistedOnceByDay = "1"
                    } else {
                        dayst.IsDDvistedOnceByDay = "0"
                    }
                    dayst.visit_distributor_date_time = AppUtils.getCurrentDateTime()
                    dayst.visit_distributor_id = Pref.visit_distributor_id
                    dayst.visit_distributor_name = Pref.visit_distributor_name
                    if (isShop) {
                        //dayst.latitude = nearByShop.shopLat.toString()
                        //dayst.longitude = nearByShop.shopLong.toString()
                        dayst.shop_type = nearByShop.type
                        dayst.shop_id = nearByShop.shop_id
                        dayst.isStart = "0"
                        dayst.isEnd = "1"
                        dayst.sale_Value = saleValue
                        dayst.remarks = ""
                    } else {
                        //dayst.latitude = nearByDD.dd_latitude.toString()
                        //dayst.longitude = nearByDD.dd_longitude.toString()
                        dayst.shop_type = "4"
                        dayst.shop_id = nearByDD.dd_id
                        dayst.isStart = "0"
                        dayst.isEnd = "1"
                        dayst.sale_Value = saleValue
                        dayst.remarks = ""
                    }

                    var addr = ""
                    try {
                        addr = LocationWizard.getAdressFromLatlng(
                            mContext,
                            loc.latitude,
                            loc.longitude
                        )
                    } catch (ex: Exception) {
                        addr = ""
                    }


                    val repository = DayStartEndRepoProvider.dayStartRepositiry()
                    BaseActivity.compositeDisposable.add(
                        repository.dayStart(dayst)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                Timber.d(
                                    "DayEnd (DashboardFrag): DayEnded Success status " + result.status + " lat " +
                                            loc.latitude.toString() + " long " + loc.longitude.toString() + " addr " + addr + " " + AppUtils.getCurrentDateTime()
                                )
                                val response = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (response.status == NetworkConstant.SUCCESS) {
                                    //(mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                    //voiceAttendanceMsg("Hi, your day end marked successfully.")
                                    isStartOrEndDay(true)
                                }
                            }, { error ->
                                if (error == null) {
                                    Timber.d("DayEnd (DashboardFrag) : ERROR " + "UNEXPECTED ERROR IN DayStart API " + AppUtils.getCurrentDateTime())
                                } else {
                                    Timber.d("DayEnd (DashboardFrag) : ERROR " + error.localizedMessage + " " + AppUtils.getCurrentDateTime())
                                    error.printStackTrace()
                                }
                                progress_wheel.stopSpinning()
                            })
                    )

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    progress_wheel.stopSpinning()
                }
            } else {
                (mContext as DashboardActivity).showSnackMessage("Please enter sale value")
                progress_wheel.stopSpinning()
            }

        })
        simpleDialog.show()

    }


    fun isStartOrEndDay(isDayEnd: Boolean) {
        try {
            progress_wheel.spin()
            //disable screen
            requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
            if (isDayEnd) {
                Pref.IsDayEndBackPressedRestrict = true
            }

            Pref.DayStartMarked = false
            Pref.DayEndMarked = false
            Pref.DayStartShopType = ""
            Pref.DayStartShopID = ""
            //Pref.IsDDvistedOnceByDay=false
            val repository = DayStartEndRepoProvider.dayStartRepositiry()
            BaseActivity.compositeDisposable.add(
                repository.dayStartEndStatus(AppUtils.getCurrentDateyymmdd())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        Timber.d("DashboardFragment isStartOrEndDay : RESPONSE " + result.status + " " + AppUtils.getCurrentDateTime())
                        val response = result as StatusDayStartEnd
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                Pref.DayStartMarked = response.DayStartMarked!!
                                Pref.DayEndMarked = response.DayEndMarked!!
                                Pref.DayStartShopType = response.day_start_shop_type!!
                                Pref.DayStartShopID = response.day_start_shop_id!!
                                if (Pref.IsDDvistedOnceByDay != true) {
                                    Pref.IsDDvistedOnceByDay = response.IsDDvistedOnceByDay!!
                                }
                                uiThread {
                                    progress_wheel.stopSpinning()
                                    //Pref.IsshowDayStartSelfie=true
                                    //Pref.IsshowDayEndSelfie=true

                                    //enable screen
                                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    if (isDayEnd == false) {
                                        if (Pref.IsshowDayStartSelfie) {
                                            isCameraDayStart = true
                                            initPermissionCheck()
                                        } else {
                                            // 27-08-21 For ITC
                                            val simpleDialog = Dialog(mContext)
                                            simpleDialog.setCancelable(false)
                                            simpleDialog.getWindow()!!
                                                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                            simpleDialog.setContentView(R.layout.dialog_message)
                                            val dialogHeader =
                                                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                            val dialog_yes_no_headerTV =
                                                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                            dialog_yes_no_headerTV.text =
                                                "Hi " + Pref.user_name!! + "!"
                                            dialogHeader.text = "Thanks, day started successfully."
                                            val dialogYes =
                                                simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                            dialogYes.setOnClickListener({ view ->
                                                simpleDialog.cancel()
                                                callShopActivityApiForActivityCheck()
                                            })
                                            simpleDialog.show()
//                                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")

                                            Pref.DayStartTime = System.currentTimeMillis().toString()
                                            timerTV = view!!.findViewById(R.id.tv_dash_frag_timer)
                                            if(Pref.IsShowMarketSpendTimer){
                                                timerTV.visibility = View.VISIBLE
                                                try{
                                                    if(!Pref.DayStartTime.equals("")){
                                                        var currentTime = System.currentTimeMillis().toString()
                                                        var timeDiff = AppUtils.getTimeFromTimeSpan(Pref.DayStartTime,System.currentTimeMillis().toString())
                                                        timeDiff = timeDiff.split(":").get(0)+":"+timeDiff.split(":").get(1)
                                                        timerTV.text = "$timeDiff"
                                                    }
                                                }catch (ex:Exception){
                                                    Timber.d("ex timer text")
                                                }
                                            }else{
                                                timerTV.visibility = View.GONE
                                            }

                                            voiceAttendanceMsg(AppUtils.hiFirstNameText() + " day started successfully.")
                                        }
                                    }
                                    if (isDayEnd == true) {
                                        if (Pref.IsshowDayEndSelfie) {
                                            isCameraDayStart = false
                                            initPermissionCheck()
                                        } else {
                                            // 27-08-21 For ITC
                                            //(mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")

                                            Handler().postDelayed(Runnable {
                                                //callShopDurationApi()
                                                //syncShopListOnebyOne()
                                                Pref.DayStartTime = ""
                                                syncGpsNetData()
                                            }, 350)





                                            /// val simpleDialog = Dialog(mContext)
                                            /// simpleDialog.setCancelable(false)
                                            /// simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                            /// simpleDialog.setContentView(R.layout.dialog_message)
                                            /// val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                            ///val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                            ///dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                                            ///dialogHeader.text = "Thanks,  day ended Successfully."
                                            ///val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                            ///dialogYes.setOnClickListener({ view ->
                                            ///simpleDialog.cancel()
                                            //(mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                            /*Handler(Looper.getMainLooper()).postDelayed({
                                                            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                                        }, 2000)*/
                                            ///})
                                            //simpleDialog.show()
//                                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")

                                            //voiceAttendanceMsg(AppUtils.hiFirstNameText() +" day ended successfully.")

                                            /*  Handler(Looper.getMainLooper()).postDelayed({
                                                        (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                                    }, 2000)*/
                                        }
                                    }
                                }
                            }

                        } else {
                            progress_wheel.stopSpinning()
                            //getListFromDatabase()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            //enable screen
                            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }, { error ->
                        if (error == null) {
                            Timber.d("DashboardFragment isStartOrEndDay : ERROR " + "UNEXPECTED ERROR IN DayStartEnd API")
                        } else {
                            Timber.d("DashboardFragment isStartOrEndDay : ERROR " + error.localizedMessage)
                            error.printStackTrace()
                        }
                        //enable screen
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong)+".")
                        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progress_wheel.stopSpinning()
                        //getListFromDatabase()
                    })
            )
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            //getListFromDatabase()
            progress_wheel.stopSpinning()
            //enable screen
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong)+"..")
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(
                msg,
                TextToSpeech.QUEUE_FLUSH,
                null
            )
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Day Start", "TTS error in converting Text to Speech!");
        }
    }


    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {

        //begin mantis id 26741 Storage permission updation Suman 22-08-2023
        var permissionList = arrayOf<String>( Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissionList += Manifest.permission.READ_MEDIA_IMAGES
            permissionList += Manifest.permission.READ_MEDIA_AUDIO
            permissionList += Manifest.permission.READ_MEDIA_VIDEO
        }else{
            permissionList += Manifest.permission.WRITE_EXTERNAL_STORAGE
            permissionList += Manifest.permission.READ_EXTERNAL_STORAGE
        }
//end mantis id 26741 Storage permission updation Suman 22-08-2023

        permissionUtils = PermissionUtils(
            mContext as Activity,
            object : PermissionUtils.OnPermissionListener {
                override fun onPermissionGranted() {
                    //showPictureDialog()
                    launchCamera()
                }

                override fun onPermissionNotGranted() {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
                }

            },permissionList)// arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(
                mContext as DashboardActivity
            )
        ) {
            /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/

            (mContext as DashboardActivity).captureFrontImage()
        }
    }

    //fun setCameraImage(file: File) {
    fun setCameraImage(filePath: String) {
        var file = File(filePath)

        if (file == null || TextUtils.isEmpty(file.absolutePath)) {
            (mContext as DashboardActivity).showSnackMessage("Invalid Image")
            return
        }
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }


        if (isCameraDayStart && Pref.IsshowDayStartSelfie) {
            //Toaster.msgShort(mContext,"isCameraDayStart true")
            val repository = DashboardRepoProvider.provideDashboardImgRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                repository.dayStartWithImage(file.absolutePath, mContext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        val response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!
                                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader =
                                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV =
                                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                            //dialogHeader.text = "Thanks! Updated Successfully."
                            dialogHeader.text = "Thanks, day started successfully."
                            val dialogYes =
                                simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                            voiceAttendanceMsg("Hi, " + AppUtils.hiFirstNameText() + " day started successfully.")
                        } else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        }
                    }, { error ->
                        error.printStackTrace()
                        BaseActivity.isApiInitiated = false
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        } else if (isCameraDayStart == false && Pref.IsshowDayEndSelfie) {
            //Toaster.msgShort(mContext,"isCameraDayStart false")
            val repository = DashboardRepoProvider.provideDashboardImgRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                repository.dayEndWithImage(file.absolutePath, mContext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        val response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            //19-08-21 Added for force day end
//                                     performLogout()
                            // 27-08-21 For ITC
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!
                                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader =
                                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV =
                                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                            //dialogHeader.text = "Thanks! Updated Successfully."
                            dialogHeader.text = "Thanks! day ended Successfully."
                            val dialogYes =
                                simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    (mContext as DashboardActivity).loadFragment(
                                        FragType.LogoutSyncFragment,
                                        false,
                                        ""
                                    )
                                }, 2000)
                            })
                            simpleDialog.show()
//                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                            voiceAttendanceMsg("Hi, " + Pref.user_name + "!" + " day ended successfully.")

                            /*     Handler(Looper.getMainLooper()).postDelayed({
                                        (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                    }, 2000)*/

                        } else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        }
                    }, { error ->
                        error.printStackTrace()
                        BaseActivity.isApiInitiated = false
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        }

    }


    fun setImage(filePath: String) {
        val file = File(filePath)
        var newFile: File? = null
        progress_wheel.spin()
        doAsync {
            val processImage = ProcessImageUtils_v1(mContext, file, 50)
            newFile = processImage.ProcessImageSelfie()
            uiThread {
                if (newFile != null) {
                    Timber.e("=========Image from new technique==========")
                    val fileSize = AppUtils.getCompressImage(filePath)
                    setCameraImage(filePath)
                } else {
                    // Image compression
                    val fileSize = AppUtils.getCompressImage(filePath)
                    setCameraImage(filePath)
                }
            }
        }
    }


    private fun getLocforDD() {
        if (AppUtils.isOnline(mContext) || true) {
            if (AppUtils.mLocation != null) {
                if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        getNearyShopListDD(AppUtils.mLocation!!)
                    } else {
                        //getDDList(AppUtils.mLocation!!)
                        singleLocationDD()
                    }
                } else {
                    Timber.d("=====Inaccurate current location (Local Shop List)=====")
                    singleLocationDD()
                }
            } else {
                Timber.d("=====null location (Local Shop List)======")
                singleLocationDD()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
    }

    private fun singleLocationDD() {
        progress_wheel.spin()
        isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
            object : SingleShotLocationProvider.LocationCallback {
                override fun onStatusChanged(status: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onProviderEnabled(status: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onProviderDisabled(status: String) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onNewLocationAvailable(location: Location) {
                    if (isGetLocation == -1) {
                        isGetLocation = 0
                        if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                            (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                            progress_wheel.stopSpinning()
                        } else
                            getNearyShopListDD(location)
                    }
                }

            })

        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(
                            "GPS data to show nearby party is inaccurate. Please stop " +
                                    "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list."
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

    private fun getNearyShopListDD(location: Location) {
        //var nearBy: Double = Pref.shopLocAccuracy.toDouble()
//        var nearBy: Double = 4000.00
        var nearBy: Double = Pref.DistributorGPSAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        /*    if (newList != null && newList.size > 0) {
                for (i in 0 until newList.size) {
                    val shopLat: Double = newList[i].shopLat
                    val shopLong: Double = newList[i].shopLong
                    if (shopLat != null && shopLong != null) {
                        val shopLocation = Location("")
                        shopLocation.latitude = shopLat
                        shopLocation.longitude = shopLong
                        val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                        if (isShopNearby) {
                            if ((location.distanceTo(shopLocation)) < nearBy) {
                                nearBy = location.distanceTo(shopLocation).toDouble()
                                finalNearByShop = newList[i]
                            }
                            //startDay(newList[i], location)
                            //break
                        }
                    }
                }

            } else {
                //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
            }*/

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                        location,
                        ddLocation,
                        Pref.DistributorGPSAccuracy.toInt()
                    )
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }


        //finalNearByDD=newDDList[5]

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            Pref.IsDDvistedOnceByDay = true
            Pref.visit_distributor_id = finalNearByDD.dd_id.toString()
            Pref.visit_distributor_name = finalNearByDD.dd_name.toString()
            Pref.visit_distributor_date_time = AppUtils.getCurrentDateTime()
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader =
                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV =
                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
            //dialogHeader.text = "Distributor Visited..."
            dialogHeader.text = "Thanks! Point visited Successfully."
            var addr = ""
            try {
                addr = LocationWizard.getAdressFromLatlng(
                    mContext,
                    finalNearByDD.dd_latitude!!.toDouble(),
                    finalNearByDD.dd_longitude!!.toDouble()
                )
            } catch (ex: Exception) {
                addr = ""
            }
            Timber.d(
                "PointVisit (DashboardFrag): PointVisit Success " + " lat " +
                        finalNearByDD.dd_latitude.toString() + " long " + finalNearByDD.dd_longitude + " addr " + addr + " " + AppUtils.getCurrentDateTime()
            )
            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("Distributor Visited")
            voiceAttendanceMsg(AppUtils.hiFirstNameText() + "Point Visited")
//            ddDay(finalNearByShop, finalNearByDD, location, false)
        } /*else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
//            ddDay(finalNearByShop, finalNearByDD, location, true)
            *//*Pref.IsDDvistedOnceByDay = true
            Pref.visit_distributor_id = finalNearByShop.shop_id.toString()
            Pref.visit_distributor_name = finalNearByShop.shopName.toString()
            Pref.visit_distributor_date_time = AppUtils.getCurrentDateTime()*//*
        }*/ else {
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader =
                simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV =
                simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
            dialogHeader.text = "No nearby Shop/Point found..."
            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }


    }

    fun ddDay(
        nearByShop: AddShopDBModelEntity,
        nearByDD: AssignToDDEntity,
        loc: Location,
        isShop: Boolean
    ) {
        progress_wheel.spin()
        try {
            var dayst: DaystartDayendRequest = DaystartDayendRequest()
            dayst.user_id = Pref.user_id
            dayst.session_token = Pref.session_token
            dayst.date = AppUtils.getCurrentDateTime()
            dayst.location_name =
                LocationWizard.getNewLocationName(mContext, loc.latitude, loc.longitude)
            dayst.latitude = loc.latitude.toString()
            dayst.longitude = loc.longitude.toString()
            dayst.IsDDvistedOnceByDay = Pref.IsDDvistedOnceByDay.toString()
            dayst.visit_distributor_date_time = AppUtils.getCurrentDateTime()
            dayst.visit_distributor_id = nearByDD.dd_id
            dayst.visit_distributor_name = nearByDD.dd_name

            val repository = DayStartEndRepoProvider.dayStartRepositiry()
            BaseActivity.compositeDisposable.add(
                repository.dayStart(dayst)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        Timber.d("DashboardFragment DayEnd : RESPONSE " + result.status)
                        val response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
//                                            voiceAttendanceMsg("Hi, your day end marked successfully.")
                        }
                    }, { error ->
                        if (error == null) {
                            Timber.d("DashboardFragment DayEnd : ERROR " + "UNEXPECTED ERROR IN DayStart API")
                        } else {
                            Timber.d("DashboardFragment DayEnd : ERROR " + error.localizedMessage)
                            error.printStackTrace()
                        }
                        progress_wheel.stopSpinning()
                    })
            )

        } catch (ex: Exception) {
            ex.printStackTrace()
            progress_wheel.stopSpinning()
        }


    }


    /////////////////////////////// 10-09-2021
    var cropToFrameTransform: Matrix? = Matrix()
    var faceDetector: FaceDetector? = null
    private val TF_OD_API_MODEL_FILE = "mobile_face_net.tflite"
    val TF_OD_API_IS_QUANTIZED = false
    val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"
    val TF_OD_API_INPUT_SIZE = 112

    private var rgbFrameBitmap: Bitmap? = null
    private var faceBmp: Bitmap? = null
    protected var previewWidth = 0
    protected var previewHeight = 0
    private var portraitBmp: Bitmap? = null

    fun faceDetectorSetUp() {
        try {
            FaceStartActivity.detector = TFLiteObjectDetectionAPIModel.create(
                mContext.getAssets(),
                TF_OD_API_MODEL_FILE,
                TF_OD_API_LABELS_FILE,
                TF_OD_API_INPUT_SIZE,
                TF_OD_API_IS_QUANTIZED
            )
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (e: IOException) {
            e.printStackTrace()
            //LOGGER.e(e, "Exception initializing classifier!");
            val toast =
                Toast.makeText(mContext, "Classifier could not be initialized", Toast.LENGTH_SHORT)
            toast.show()
            //finish()
        }
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            //.setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)

        faceDetector = detector
    }


    fun getPicUrl() {
        //31-08-2021
        Timber.d("getPicUrl ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
        BaseActivity.isApiInitiated = false
        val repository = GetUserListPhotoRegProvider.provideUserListPhotoReg()
        BaseActivity.compositeDisposable.add(
            repository.getUserFacePicUrlApi(Pref.user_id!!, Pref.session_token!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as UserFacePicUrlResponse
                    if (response.status == NetworkConstant.SUCCESS) {

                        CustomStatic.FaceUrl = response.face_image_link

                        //val intent = Intent(mContext, FaceStartActivity::class.java)
                        //startActivityForResult(intent, 111)


                        //var bitmap :Bitmap? = null
                        //registerFace(bitmap);
                        println("reg_face - GetImageFromUrl called" + AppUtils.getCurrentDateTime());
                        Timber.d("DashFragment : FaceRegistration/FaceMatch " + response.status.toString() + ", ${Pref.isCalledFromStart} : " + ", Success: ")
                        GetImageFromUrl().execute(CustomStatic.FaceUrl)
                    } else {
                        BaseActivity.isApiInitiated = false
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_reg_face))
                        progress_wheel.stopSpinning()
                        Timber.d("AddAttendanceFragment : FaceRegistration/FaceMatch : " + response.status.toString() + ", : " + ", Failed: ")
                    }
                }, { error ->
                    if (error != null) {
                        Timber.d("AddAttendanceFragment : FaceRegistration/FaceMatch : " + " : " + ", ERROR: " + error.localizedMessage)
                    }
                    BaseActivity.isApiInitiated = false
                })
        )
    }

    inner class GetImageFromUrl : AsyncTask<String?, Void?, Bitmap?>() {
        fun GetImageFromUrl() {
            //this.imageView = img;
        }

        override fun doInBackground(vararg url: String?): Bitmap {
            var bitmappppx: Bitmap? = null
            val stringUrl = url[0]
            bitmappppx = null
            val inputStream: InputStream
            try {
                inputStream = URL(stringUrl).openStream()
                bitmappppx = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmappppx!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            println("reg_face - registerFace called" + AppUtils.getCurrentDateTime());
            Timber.d("GetImageFromUrl : face pic processed ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " + AppUtils.getCurrentDateTime())
            registerFace(result)
        }

    }

    private fun registerFace(mBitmap: Bitmap?) {
        Timber.d("registerFace " + AppUtils.getCurrentDateTime())
        progress_wheel.stopSpinning()
        progress_wheel.spin()
        //BaseActivity.isApiInitiated=false
        println("reg_face - add_attendance_registerFace" + AppUtils.getCurrentDateTime());
        try {
            if (mBitmap == null) {
                //Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show()
                return
            }
            //ivFace.setImageBitmap(mBitmap)
            previewWidth = mBitmap.width
            previewHeight = mBitmap.height
            rgbFrameBitmap =
                Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            portraitBmp = mBitmap
            val image = InputImage.fromBitmap(mBitmap, 0)
            faceBmp = Bitmap.createBitmap(
                TF_OD_API_INPUT_SIZE,
                TF_OD_API_INPUT_SIZE,
                Bitmap.Config.ARGB_8888
            )
            faceDetector?.process(image)
                ?.addOnSuccessListener(OnSuccessListener<List<Face>> { faces ->
                    if (faces.size == 0) {
                        println("reg_face - dashboard_frag no face detected" + AppUtils.getCurrentDateTime());
                        return@OnSuccessListener
                    }
                    Handler().post {
                        object : Thread() {
                            override fun run() {
                                //action
                                println("reg_face - dashboard_frag face detected" + AppUtils.getCurrentDateTime());
                                Timber.d("onFacesDetected ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " + AppUtils.getCurrentDateTime())
                                onFacesDetected(1, faces, true) //no need to add currtime
                            }
                        }.start()
                    }
                })


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun onFacesDetected(currTimestamp: Long, faces: List<Face>, add: Boolean) {
        try {
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.0f
            val mappedRecognitions: MutableList<SimilarityClassifier.Recognition> = LinkedList()


            //final List<Classifier.Recognition> results = new ArrayList<>();

            // Note this can be done only once
            val sourceW = rgbFrameBitmap!!.width
            val sourceH = rgbFrameBitmap!!.height
            val targetW = portraitBmp!!.width
            val targetH = portraitBmp!!.height
            val transform = createTransform(
                sourceW,
                sourceH,
                targetW,
                targetH,
                90
            )
            val mutableBitmap = portraitBmp!!.copy(Bitmap.Config.ARGB_8888, true)
            val cv = Canvas(mutableBitmap)

            // draws the original image in portrait mode.
            cv.drawBitmap(rgbFrameBitmap!!, transform!!, null)
            val cvFace = Canvas(faceBmp!!)
            val saved = false
            for (face in faces) {
                //results = detector.recognizeImage(croppedBitmap);
                val boundingBox = RectF(face.boundingBox)

                //final boolean goodConfidence = result.getConfidence() >= minimumConfidence;
                val goodConfidence = true //face.get;
                if (boundingBox != null && goodConfidence) {

                    // maps crop coordinates to original
                    cropToFrameTransform?.mapRect(boundingBox)

                    // maps original coordinates to portrait coordinates
                    val faceBB = RectF(boundingBox)
                    transform.mapRect(faceBB)

                    // translates portrait to origin and scales to fit input inference size
                    //cv.drawRect(faceBB, paint);
                    val sx = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.width()
                    val sy = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.height()
                    val matrix = Matrix()
                    matrix.postTranslate(-faceBB.left, -faceBB.top)
                    matrix.postScale(sx, sy)
                    cvFace.drawBitmap(portraitBmp!!, matrix, null)

                    //canvas.drawRect(faceBB, paint);
                    var label = ""
                    var confidence = -1f
                    var color = Color.BLUE
                    var extra: Any? = null
                    var crop: Bitmap? = null
                    if (add) {
                        try {
                            crop = Bitmap.createBitmap(
                                portraitBmp!!,
                                faceBB.left.toInt(),
                                faceBB.top.toInt(),
                                faceBB.width().toInt(),
                                faceBB.height().toInt()
                            )
                        } catch (eon: java.lang.Exception) {
                            //runOnUiThread(Runnable { Toast.makeText(mContext, "Failed to detect", Toast.LENGTH_LONG) })
                        }
                    }
                    val startTime = SystemClock.uptimeMillis()
                    val resultsAux = FaceStartActivity.detector.recognizeImage(faceBmp, add)
                    val lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
                    if (resultsAux.size > 0) {
                        val result = resultsAux[0]
                        extra = result.extra
                        //          Object extra = result.getExtra();
    //          if (extra != null) {
    //            LOGGER.i("embeeding retrieved " + extra.toString());
    //          }
                        val conf = result.distance
                        if (conf < 1.0f) {
                            confidence = conf
                            label = result.title
                            color = if (result.id == "0") {
                                Color.GREEN
                            } else {
                                Color.RED
                            }
                        }
                    }
                    val flip = Matrix()
                    flip.postScale(1f, -1f, previewWidth / 2.0f, previewHeight / 2.0f)

                    //flip.postScale(1, -1, targetW / 2.0f, targetH / 2.0f);
                    flip.mapRect(boundingBox)
                    val result = SimilarityClassifier.Recognition(
                        "0", label, confidence, boundingBox
                    )
                    result.color = color
                    result.location = boundingBox
                    result.extra = extra
                    result.crop = crop
                    mappedRecognitions.add(result)
                }
            }

            //    if (saved) {
//      lastSaved = System.currentTimeMillis();
//    }

            Log.e("xc", "startabc")
            val rec = mappedRecognitions[0]
            FaceStartActivity.detector.register("", rec)
            val intent = Intent(mContext, DetectorActivity::class.java)
            Timber.d("171 hit ")
            startActivityForResult(intent, 171)
//        startActivity(new Intent(this,DetectorActivity.class));
//        finish();

            // detector.register("Sakil", rec);
            /*   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivFace.setImageBitmap(rec.getCrop());
                        //showAddFaceDialog(rec);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
                        ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image);
                        TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
                        EditText etName = dialogLayout.findViewById(R.id.dlg_input);

                        tvTitle.setText("Register Your Face");
                        ivFace.setImageBitmap(rec.getCrop());
                        etName.setHint("Please tell your name");
                        detector.register("sam", rec); //for register a face

                        //button.setPressed(true);
                        //button.performClick();
                    }

                });*/

            // updateResults(currTimestamp, mappedRecognitions);
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.d("onfacedetected err ${e.printStackTrace()}")
        }
    }

    fun createTransform(
        srcWidth: Int,
        srcHeight: Int,
        dstWidth: Int,
        dstHeight: Int,
        applyRotation: Int
    ): Matrix? {
        val matrix = Matrix()
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                // LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

//        // Account for the already applied rotation, if any, and then determine how
//        // much scaling is needed for each axis.
//        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
//        final int inWidth = transpose ? srcHeight : srcWidth;
//        final int inHeight = transpose ? srcWidth : srcHeight;
        if (applyRotation != 0) {

            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }
        return matrix
    }

    private fun startTvClick() {
        Timber.d("startTvClick ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }
        else if (Pref.isStartCall == false && Pref.DayStartMarked == false && Pref.isAddAttendence) {
            Timber.d("startTvClick elside ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_yes_no)
            val dialogHeader =
                simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV =
                simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
            dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
            dialogHeader.text = "Are you sure ?"
            val dialogYes =
                simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
                //if(Pref.IsshowDayStartSelfie){
                //isCameraDayStart=true
                //initPermissionCheck()
                //}else{
                progress_wheel.spin()
                Pref.isCalledFromStart = true
                Timber.d("isCalledFromStart startTvClick check ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart} " +" "+ AppUtils.getCurrentDateTime())
                getPicUrl()
                //}
            })
            dialogNo.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
        }
        else {
            println("reg_face - start_tv" + AppUtils.getCurrentDateTime());
            Timber.d("startTvClick else ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart}" +" "+ AppUtils.getCurrentDateTime())
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            } else {
                Timber.d("startTvClick check ${Pref.isAddAttendence}" +" "+ AppUtils.getCurrentDateTime())
                if (!Pref.isAddAttendence) {
                    Timber.d("isAddAttendence if ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart}" +" "+ AppUtils.getCurrentDateTime())
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                    dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                    dialogHeader.text = "Please mark your attendance."
                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Please mark your attendance")
                }
                else {
                    Timber.d("startTvClick check1 ${Pref.DayStartMarked}" +" "+ AppUtils.getCurrentDateTime())
                    if (!Pref.DayStartMarked) {
                        Timber.d("startTvClick DayStartMarked checkk calling getLocforStart ${Pref.isAddAttendence} ${Pref.DayStartMarked}" +" "+ AppUtils.getCurrentDateTime())
                        Timber.d("isAddAttendence else ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart}" +" "+ AppUtils.getCurrentDateTime())
                        getLocforStart()

                        /*val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_yes_no)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                            dialogHeader.text = "Are you sure ?"
                            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                getLocforStart()
                            })
                            dialogNo.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()*/

                    } else {
                        // 27-08-21 For ITC
                        Timber.d("startTvClick DayStartMarked check ${Pref.isAddAttendence} ${Pref.DayStartMarked}" +" "+ AppUtils.getCurrentDateTime())
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!
                            .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader =
                            simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV =
                            simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                        dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                        dialogHeader.text = "Your Day started already."
                        val dialogYes =
                            simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Day started already")
                    }
                }
            }
        }
    }

    private fun endTvClick() {
        Timber.d("endTvClick ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isEndCall} " +" "+ AppUtils.getCurrentDateTime())
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        } else if (Pref.isEndCall == false && Pref.DayEndMarked == false && Pref.DayStartMarked == true) {

            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_yes_no)
            val dialogHeader =
                simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV =
                simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
            dialogHeader.text = "Are you sure to Exit for the Day?"
            val dialogYes =
                simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
                //if(Pref.IsshowDayStartSelfie){
                //isCameraDayStart=true
                //initPermissionCheck()
                //}else{
                progress_wheel.spin()
                Pref.isCalledFromStart = false
                Timber.d("isCalledFromStart from endTvClick check ${Pref.isAddAttendence} ${Pref.DayStartMarked} ${Pref.isStartCall} ${Pref.isEndCall} ${Pref.isCalledFromStart}" +" "+ AppUtils.getCurrentDateTime())
                getPicUrl()
                //}
            })
            dialogNo.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
        } else {
            if (Pref.DayEndMarked) {
                // 27-08-21 For ITC
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_message)
                val dialogHeader =
                    simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV =
                    simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                dialogHeader.text = "Your Day already ended..."
                val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                    (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")

                })
                simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Day already ended")
            } else {
                //Pref.IsDDvistedOnceByDay=true
                if (Pref.DayStartMarked) {

                    getLocforEnd()

//                        if (Pref.IsDDvistedOnceByDay) {// 01-09-2021  end date time , point visit not checking
                    /*val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_yes_no)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                        dialogHeader.text = "Are you sure to Exit for the Day?"
                        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                        val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()

                            getLocforEnd()



                        })
                        dialogNo.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()*/


//                        }
                    /* else {
                             // 27-08-21 For ITC
                             val simpleDialog = Dialog(mContext)
                             simpleDialog.setCancelable(false)
                             simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                             simpleDialog.setContentView(R.layout.dialog_message)
                             val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                             val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                             dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()
                             dialogHeader.text = "Please visit Point at once..."
                             val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                             dialogYes.setOnClickListener({ view ->
                                 simpleDialog.cancel()
                             })
                             simpleDialog.show()
                         }*/

                } else {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                    dialogHeader.text = "Please start your day..."
                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Please start your day")
                    //test
                }
            }
        }
    }

    private fun pointTvClick() {
        if (!AppUtils.isOnline(mContext) && false) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        } else {
            if (!Pref.isAddAttendence) {
                // 27-08-21 For ITC
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_message)
                val dialogHeader =
                    simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV =
                    simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                dialogHeader.text = "Please Marked Your Attendance First..."
                val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Please Marked Your Attendance First")
            } else {
                if (!Pref.IsDDvistedOnceByDay) {
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_yes_no)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                    dialogHeader.text = "Wish to Visit Distributor Location Right Now?"
                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                    val dialogNo =
                        simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                        getLocforDD()
                    })
                    dialogNo.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
                } else {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!
                        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader =
                        simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV =
                        simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                    dialogHeader.text = "You are already visited once..."
                    val dialogYes =
                        simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()

                }

            }
        }
    }

    private fun checkAutoRevisit() {
        var revCount = 0
        var distance = 0.7
        if (distance * 1000 > Pref.autoRevisitDistance.toDouble()) {
            val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
            if (allShopList != null && allShopList.size > 0) {
                for (i in 0 until allShopList.size) {
                    val shopLat: Double = allShopList[i].shopLat
                    val shopLong: Double = allShopList[i].shopLong
                    if (shopLat != null && shopLong != null) {
                        val shopLocation = Location("")
                        shopLocation.latitude = shopLat
                        shopLocation.longitude = shopLong
                        try {
                            if (AppUtils.mLocation == null) {
                                var loc = Location("")
                                loc.latitude = Pref.current_latitude.toDouble()
                                loc.longitude = Pref.current_longitude.toDouble()
                                AppUtils.mLocation = loc
                            }
                        } catch (ex: Exception) {

                        }
                        val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                            AppUtils.mLocation,
                            shopLocation,
                            Pref.autoRevisitDistance.toInt()
                        )
                        Timber.e(
                            "Distance 1 from shop " + allShopList[i].shopName + " location to current location============> " + AppUtils.mLocation?.distanceTo(
                                shopLocation
                            ) + " Meter"
                        )
                        val distance = LocationWizard.getDistance(
                            shopLat,
                            shopLong,
                            Pref.current_latitude.toDouble(),
                            Pref.current_longitude.toDouble()
                        )
                        Timber.e("Distance 2 from shop " + allShopList[i].shopName + " location to current location============> $distance KM")

                        if (isShopNearby) {
                            Timber.e("================Nearby shop " + allShopList[i].shopName + "(Location Fuzed Service)===================")
                            val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao()
                                .getShopForDay(
                                    allShopList[i].shop_id,
                                    AppUtils.getCurrentDateForShopActi()
                                )

                            if (shopActivityList == null || shopActivityList.isEmpty()) {
                                //AppUtils.changeLanguage(mContext, "en")
                                //val currentTimeStamp = System.currentTimeMillis()
                                shop_id = allShopList[i].shop_id
                                AppUtils.isAutoRevisit = true
                                Timber.e("revisitShop called")
                                revisitShop()
                                Timber.e("revisitShop returned")
                                revCount++
                                shop_id = ""
                                Timber.e("revisitShop stopSpinning")
                                break
                            } else
                                Timber.e("================" + allShopList[i].shopName + " is visiting now normally (Location Fuzed Service)===================")
                        }
                    }
                }
                //progress_wheel.stopSpinning()
            }
            else {
                //progress_wheel.stopSpinning()
            }
        }
        else {
            //progress_wheel.stopSpinning()
        }
    }
    fun checkAutoRevisitManual(location:Location) {
        var nearestDist = 5000
        var nearBy: Double = Pref.DistributorGPSAccuracy.toDouble()
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        var visitedForToday = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()) as List<ShopActivityEntity>
        if(visitedForToday.size > 0 && newList.size>0){
            for(i in 0..visitedForToday.size-1) {
                var match = false
                var pos = 0
                for (j in 0..newList.size - 1) {
                    if (newList.get(j).shop_id.equals(visitedForToday.get(i).shopid)) {
                        match = true
                        pos = j
                        break
                    }
                }
                if (match) {
                    newList.removeAt(pos)
                }
            }
        }


        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(
                        location,
                        shopLocation,
                        Pref.DistributorGPSAccuracy.toInt()
                    )
                    var dist = location.distanceTo(shopLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                    } else {
                        if (dist < nearestDist) {
                            nearestDist = dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

       if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
           shop_id = finalNearByShop.shop_id
           AppUtils.isAutoRevisit = true
           revisitShop()
           shop_id = ""
        }else{
           return
        }

    }


    private fun changeLocale() {
        val intent = Intent()
        intent.action = "CHANGE_LOCALE_BROADCAST"
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
    }


    private fun revisitShop() {

        Timber.e("revisitShop started")
        try {
            val shopActivityEntity = AppDatabase.getDBInstance()!!.shopActivityDao()
                .getShopForDay(shop_id, AppUtils.getCurrentDateForShopActi())
            val imageUpDateTime = AppUtils.getCurrentISODateTime()

            val mAddShopDBModelEntity =
                AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)

            if (shopActivityEntity.isEmpty() || shopActivityEntity[0].date != AppUtils.getCurrentDateForShopActi()) {
                val mShopActivityEntity = ShopActivityEntity()
                AppUtils.changeLanguage(mContext, "en")
                mShopActivityEntity.startTimeStamp = System.currentTimeMillis().toString()
                changeLocale()
                mShopActivityEntity.isUploaded = false
                mShopActivityEntity.isVisited = true
                mShopActivityEntity.shop_name = mAddShopDBModelEntity?.shopName
                mShopActivityEntity.duration_spent = "00:00:00"
                mShopActivityEntity.date = AppUtils.getCurrentDateForShopActi()
                mShopActivityEntity.shop_address = mAddShopDBModelEntity?.address
                mShopActivityEntity.shopid = mAddShopDBModelEntity?.shop_id
                mShopActivityEntity.visited_date =
                    imageUpDateTime //AppUtils.getCurrentISODateTime()
                mShopActivityEntity.isDurationCalculated = false
                if (mAddShopDBModelEntity?.totalVisitCount != null && mAddShopDBModelEntity?.totalVisitCount != "") {
                    val visitCount = mAddShopDBModelEntity?.totalVisitCount?.toInt()!! + 1
                    AppDatabase.getDBInstance()!!.addShopEntryDao()
                        .updateTotalCount(visitCount.toString(), shop_id)
                    AppDatabase.getDBInstance()!!.addShopEntryDao()
                        .updateLastVisitDate(AppUtils.getCurrentDateChanged(), shop_id)
                }

                var distance = 0.0
                var address = ""
                Timber.e("======New Distance (At auto revisit time)=========")

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shop_id)
                address = if (!TextUtils.isEmpty(shop.actual_address))
                    shop.actual_address
                else
                    LocationWizard.getNewLocationName(
                        mContext,
                        shop.shopLat.toDouble(),
                        shop.shopLong.toDouble()
                    )

                if (Pref.isOnLeave.equals("false", ignoreCase = true)) {

                    Timber.e("=====User is at work (At auto revisit time)=======")

                    val locationList = AppDatabase.getDBInstance()!!.userLocationDataDao()
                        .getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

                    //val distance = LocationWizard.getDistance(shop.shopLat, shop.shopLong, location.latitude, location.longitude)

                    val userlocation = UserLocationDataEntity()
                    userlocation.latitude = shop.shopLat.toString()
                    userlocation.longitude = shop.shopLong.toString()

                    var loc_distance = 0.0

                    if (locationList != null && locationList.isNotEmpty()) {
                        loc_distance = LocationWizard.getDistance(
                            locationList[locationList.size - 1].latitude.toDouble(),
                            locationList[locationList.size - 1].longitude.toDouble(),
                            userlocation.latitude.toDouble(),
                            userlocation.longitude.toDouble()
                        )
                    }
                    val finalDistance = (Pref.tempDistance.toDouble() + loc_distance).toString()

                    Timber.e("===Distance (At auto shop revisit time)===")
                    Timber.e("Temp Distance====> " + Pref.tempDistance)
                    Timber.e("Normal Distance====> $loc_distance")
                    Timber.e("Total Distance====> $finalDistance")
                    Timber.e("===========================================")

                    userlocation.distance = finalDistance
                    userlocation.locationName = LocationWizard.getNewLocationName(
                        mContext,
                        userlocation.latitude.toDouble(),
                        userlocation.longitude.toDouble()
                    )
                    userlocation.timestamp = LocationWizard.getTimeStamp()
                    userlocation.time = LocationWizard.getFormattedTime24Hours(true)
                    userlocation.meridiem = LocationWizard.getMeridiem()
                    userlocation.hour = LocationWizard.getHour()
                    userlocation.minutes = LocationWizard.getMinute()
                    userlocation.isUploaded = false
                    userlocation.shops = AppDatabase.getDBInstance()!!.shopActivityDao()
                        .getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toString()
                    userlocation.updateDate = AppUtils.getCurrentDateForShopActi()
                    userlocation.updateDateTime = AppUtils.getCurrentDateTime()
                    userlocation.network_status =
                        if (AppUtils.isOnline(mContext)) "Online" else "Offline"
                    userlocation.battery_percentage =
                        AppUtils.getBatteryPercentage(mContext).toString()

                    //harcoded location isUploaded true begin
                    userlocation.isUploaded = true
                    //harcoded location isUploaded true end

                    AppDatabase.getDBInstance()!!.userLocationDataDao().insertAll(userlocation)

                    Timber.e("=====Shop auto revisit data added=======")

                    Pref.totalS2SDistance =
                        (Pref.totalS2SDistance.toDouble() + userlocation.distance.toDouble()).toString()

                    distance = Pref.totalS2SDistance.toDouble()
                    Pref.totalS2SDistance = "0.0"
                    Pref.tempDistance = "0.0"
                } else {
                    Timber.e("=====User is on leave (At auto revisit time)=======")
                    distance = 0.0
                }

                Timber.e("shop to shop distance (At auto revisit time)=====> $distance")

                mShopActivityEntity.distance_travelled = distance.toString()
                mShopActivityEntity.in_time = AppUtils.getCurrentTimeWithMeredian()
                mShopActivityEntity.in_loc = address

//                AppUtils.isShopVisited = true

                Pref.isShopVisited = true

                var shopAll = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                mShopActivityEntity.shop_revisit_uniqKey =
                    Pref.user_id + System.currentTimeMillis().toString()

                AppDatabase.getDBInstance()!!.shopActivityDao().insertAll(mShopActivityEntity)

                /*Terminate All other Shop Visit*/
                val shopList = AppDatabase.getDBInstance()!!.shopActivityDao()
                    .getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                for (i in 0 until shopList.size) {
                    if (shopList[i].shopid != mShopActivityEntity.shopid && !shopList[i].isDurationCalculated) {
                        AppUtils.changeLanguage(mContext, "en")
                        val endTimeStamp = System.currentTimeMillis().toString()
                        changeLocale()
                        val duration =
                            AppUtils.getTimeFromTimeSpan(shopList[i].startTimeStamp, endTimeStamp)
                        val totalMinute = AppUtils.getMinuteFromTimeStamp(
                            shopList[i].startTimeStamp,
                            endTimeStamp
                        )
                        //If duration is greater than 20 hour then stop incrementing
                        if (totalMinute.toInt() > 20 * 60) {
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(
                                true,
                                shopList[i].shopid!!,
                                AppUtils.getCurrentDateForShopActi()
                            )
                            return
                        }
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(
                            endTimeStamp,
                            shopList[i].shopid!!,
                            AppUtils.getCurrentDateForShopActi()
                        )
                        AppDatabase.getDBInstance()!!.shopActivityDao()
                            .updateTotalMinuteForDayOfShop(
                                shopList[i].shopid!!,
                                totalMinute,
                                AppUtils.getCurrentDateForShopActi()
                            )
                        AppDatabase.getDBInstance()!!.shopActivityDao()
                            .updateTimeDurationForDayOfShop(
                                shopList[i].shopid!!,
                                duration,
                                AppUtils.getCurrentDateForShopActi()
                            )
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(
                            true,
                            shopList[i].shopid!!,
                            AppUtils.getCurrentDateForShopActi()
                        )
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(
                            AppUtils.getCurrentTimeWithMeredian(),
                            shopList[i].shopid!!,
                            AppUtils.getCurrentDateForShopActi(),
                            shopList[i].startTimeStamp
                        )
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(
                            LocationWizard.getNewLocationName(
                                mContext,
                                Pref.current_latitude.toDouble(),
                                Pref.current_longitude.toDouble()
                            ),
                            shopList[i].shopid!!,
                            AppUtils.getCurrentDateForShopActi(),
                            shopList[i].startTimeStamp
                        )

                        val netStatus = if (AppUtils.isOnline(mContext))
                            "Online"
                        else
                            "Offline"

                        val netType =
                            if (AppUtils.getNetworkType(mContext).equals("wifi", ignoreCase = true))
                                AppUtils.getNetworkType(mContext)
                            else
                                "Mobile ${AppUtils.mobNetType(mContext)}"

                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(
                            AppUtils.getDeviceName(),
                            AppUtils.getAndroidVersion(),
                            AppUtils.getBatteryPercentage(mContext).toString(),
                            netStatus,
                            netType.toString(),
                            shopList[i].shopid!!,
                            AppUtils.getCurrentDateForShopActi()
                        )
                    }
                }
            }

            AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shop_id)!![0].visited = true

            val performance = AppDatabase.getDBInstance()!!.performanceDao()
                .getTodaysData(AppUtils.getCurrentDateForShopActi())
            if (performance != null) {
                val list = AppDatabase.getDBInstance()!!.shopActivityDao()
                    .getDurationCalculatedVisitedShopForADay(
                        AppUtils.getCurrentDateForShopActi(),
                        true
                    )
                AppDatabase.getDBInstance()!!.performanceDao().updateTotalShopVisited(
                    list.size.toString(),
                    AppUtils.getCurrentDateForShopActi()
                )
                var totalTimeSpentForADay = 0
                for (i in list.indices) {
                    totalTimeSpentForADay += list[i].totalMinute.toInt()
                }
                AppDatabase.getDBInstance()!!.performanceDao().updateTotalDuration(
                    totalTimeSpentForADay.toString(),
                    AppUtils.getCurrentDateForShopActi()
                )
            } else {
                val list = AppDatabase.getDBInstance()!!.shopActivityDao()
                    .getDurationCalculatedVisitedShopForADay(
                        AppUtils.getCurrentDateForShopActi(),
                        true
                    )
                val performanceEntity = PerformanceEntity()
                performanceEntity.date = AppUtils.getCurrentDateForShopActi()
                performanceEntity.total_shop_visited = list.size.toString()
                var totalTimeSpentForADay = 0
                for (i in list.indices) {
                    totalTimeSpentForADay += list[i].totalMinute.toInt()
                }
                performanceEntity.total_duration_spent = totalTimeSpentForADay.toString()
                AppDatabase.getDBInstance()!!.performanceDao().insert(performanceEntity)
            }



            AppUtils.isAutoRevisit = false
            val intent = Intent()
            intent.action = "AUTO_REVISIT_BROADCAST"
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
            Timber.e("revisitShop ended")
        } catch (e: Exception) {
            e.printStackTrace()
            progress_wheel.stopSpinning()
        }

    }




    private fun callShopDurationApi() {
        Timber.e("visit_tag : callShopDurationApi")
        //dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        //val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        //simpleDialogProcess.show()

        var shopId = ""
        var previousShopVisitDateNumber = 0L
        var shopVisitDate = ""
        var i = 0

        if (Pref.user_id.isNullOrEmpty() || BaseActivity.isShopActivityUpdating){
            simpleDialogProcess.dismiss()
            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
        }else{
            val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)
            if (syncedShopList.isEmpty()){
                simpleDialogProcess.dismiss()
                (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
            }else{
                BaseActivity.isShopActivityUpdating = true

                val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
                val syncedShop = ArrayList<ShopActivityEntity>()

                val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()

                doAsync {

                    var counterShopList:Int = 0

                    for (k in 0 until syncedShopList.size) {

                        if (!Pref.isMultipleVisitEnable) {
                            /* Get shop activity that has completed time duration calculation*/
                            val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true, false)

                            if (shopActivity == null) {
                                val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForTodayShop(syncedShopList[k].shop_id,true, true,
                                    AppUtils.getCurrentDateForShopActi())
                                if (shop_activity != null)
                                    syncedShop.add(shop_activity)

                            } else {
                                val shopDurationData = ShopDurationRequestData()
                                shopDurationData.shop_id = shopActivity.shopid
                                shopDurationData.spent_duration = shopActivity.duration_spent
                                shopDurationData.visited_date = shopActivity.visited_date
                                shopDurationData.visited_time = shopActivity.visited_date
                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid) != null)
                                    shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid).totalVisitCount
                                else
                                    shopDurationData.total_visit_count = "1"

                                if (TextUtils.isEmpty(shopActivity.distance_travelled))
                                    shopActivity.distance_travelled = "0.0"
                                shopDurationData.distance_travelled = shopActivity.distance_travelled

                                val currentShopVisitDateNumber = AppUtils.getTimeStampFromDateOnly(shopActivity.date!!)

                                if (shopId == shopActivity.shopid && previousShopVisitDateNumber == currentShopVisitDateNumber)
                                    continue

                                shopId = shopActivity.shopid!!
                                shopVisitDate = shopActivity.date!!
                                previousShopVisitDateNumber = currentShopVisitDateNumber

                                if (!TextUtils.isEmpty(shopActivity.feedback))
                                    shopDurationData.feedback = shopActivity.feedback
                                else
                                    shopDurationData.feedback = ""

                                shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                                shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc

                                shopDurationData.next_visit_date = shopActivity.next_visit_date

                                if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                                    shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                                else
                                    shopDurationData.early_revisit_reason = ""

                                shopDurationData.device_model = shopActivity.device_model
                                shopDurationData.android_version = shopActivity.android_version
                                shopDurationData.battery = shopActivity.battery
                                shopDurationData.net_status = shopActivity.net_status
                                shopDurationData.net_type = shopActivity.net_type
                                shopDurationData.in_time = shopActivity.in_time
                                shopDurationData.out_time = shopActivity.out_time
                                shopDurationData.start_timestamp = shopActivity.startTimeStamp
                                shopDurationData.in_location = shopActivity.in_loc
                                shopDurationData.out_location = shopActivity.out_loc
                                try{
                                    shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!
                                }catch (ex:Exception){
                                    ex.printStackTrace()
                                    shopDurationData.shop_revisit_uniqKey =Pref.user_id + System.currentTimeMillis().toString()
                                }

                                //duration garbage fix
                                try{
                                    if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                                    {
                                        shopDurationData.spent_duration="00:00:10"
                                    }
                                }catch (ex:Exception){
                                    shopDurationData.spent_duration="00:00:10"
                                }

                                //Begin Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537
                                if(shopActivity.isNewShop){
                                    shopDurationData.isNewShop = 1
                                }else{
                                    shopDurationData.isNewShop = 0
                                }
                                //End Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537

                                // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 begin
                                try {
                                    var shopOb = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopDurationData.shop_id)
                                    shopDurationData.shop_lat=shopOb.shopLat.toString()
                                    shopDurationData.shop_long=shopOb.shopLong.toString()
                                    shopDurationData.shop_addr=shopOb.address.toString()
                                }catch (ex:Exception){
                                    ex.printStackTrace()
                                }
                                // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 end

                                shopDataList.add(shopDurationData)

                                //////////////////////////
                                var revisitStatusObj= ShopRevisitStatusRequestData()
                                var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                                if(data != null ){
                                    revisitStatusObj.shop_id=data.shop_id
                                    revisitStatusObj.order_status=data.order_status
                                    revisitStatusObj.order_remarks=data.order_remarks
                                    revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                                    revisitStatusList.add(revisitStatusObj)
                                }

                                counterShopList++
                                if(counterShopList > 300){
                                    //break
                                }

                            }

                            try{
                                Timber.d("====SYNC VISITED SHOP DATA (Dashboard)====")
                                Timber.d("SHOP ID======> " + shopDurationData!!.shop_id)
                                Timber.d("SPENT DURATION======> " + shopDurationData!!.spent_duration)
                                Timber.d("VISIT DATE=========> " + shopDurationData?.visited_date)
                                Timber.d("VISIT DATE TIME==========> " + shopDurationData?.visited_date)
                                Timber.d("TOTAL VISIT COUNT========> " + shopDurationData?.total_visit_count)
                                Timber.d("DISTANCE TRAVELLED========> " + shopDurationData?.distance_travelled)
                                Timber.d("FEEDBACK========> " + shopDurationData?.feedback)
                                Timber.d("isFirstShopVisited========> " + shopDurationData?.isFirstShopVisited)
                                Timber.d("distanceFromHomeLoc========> " + shopDurationData?.distanceFromHomeLoc)
                                Timber.d("next_visit_date========> " + shopDurationData?.next_visit_date)
                                Timber.d("device_model========> " + shopDurationData?.device_model)
                                Timber.d("android_version========> " + shopDurationData?.android_version)
                                Timber.d("battery========> " + shopDurationData?.battery)
                                Timber.d("net_status========> " + shopDurationData?.net_status)
                                Timber.d("net_type========> " + shopDurationData?.net_type)
                                Timber.d("in_time========> " + shopDurationData?.in_time)
                                Timber.d("out_time========> " + shopDurationData?.out_time)
                                Timber.d("start_timestamp========> " + shopDurationData?.start_timestamp)
                                Timber.d("in_location========> " + shopDurationData?.in_location)
                                Timber.d("out_location========> " + shopDurationData?.out_location)
                                Timber.d("========================================================")
                            }catch (ex:Exception){

                            }


                        }
                        else {
                            val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShopList(syncedShopList[k].shop_id, true,
                                false)

                            shopActivity?.forEach {
                                val shopDurationData = ShopDurationRequestData()
                                shopDurationData.shop_id = it.shopid
                                shopDurationData.spent_duration = it.duration_spent
                                shopDurationData.visited_date = it.visited_date
                                shopDurationData.visited_time = it.visited_date
                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid) != null)
                                    shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid).totalVisitCount
                                else
                                    shopDurationData.total_visit_count = "1"

                                if (TextUtils.isEmpty(it.distance_travelled))
                                    it.distance_travelled = "0.0"
                                shopDurationData.distance_travelled = it.distance_travelled

                                if (!TextUtils.isEmpty(it.feedback))
                                    shopDurationData.feedback = it.feedback
                                else
                                    shopDurationData.feedback = ""

                                shopDurationData.isFirstShopVisited = it.isFirstShopVisited
                                shopDurationData.distanceFromHomeLoc = it.distance_from_home_loc

                                shopDurationData.next_visit_date = it.next_visit_date

                                if (!TextUtils.isEmpty(it.early_revisit_reason))
                                    shopDurationData.early_revisit_reason = it.early_revisit_reason
                                else
                                    shopDurationData.early_revisit_reason = ""

                                shopDurationData.device_model = it.device_model
                                shopDurationData.android_version = it.android_version
                                shopDurationData.battery = it.battery
                                shopDurationData.net_status = it.net_status
                                shopDurationData.net_type = it.net_type
                                shopDurationData.in_time = it.in_time
                                shopDurationData.out_time = it.out_time
                                shopDurationData.start_timestamp = it.startTimeStamp
                                shopDurationData.in_location = it.in_loc
                                shopDurationData.out_location = it.out_loc
                                shopDurationData.shop_revisit_uniqKey=it.shop_revisit_uniqKey!!

                                //duration garbage fix
                                try{
                                    if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                                    {
                                        shopDurationData.spent_duration="00:00:10"
                                    }
                                }catch (ex:Exception){
                                    shopDurationData.spent_duration="00:00:10"
                                }

                                //Begin Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537
                                if(it.isNewShop){
                                    shopDurationData.isNewShop = 1
                                }else{
                                    shopDurationData.isNewShop = 0
                                }
                                //End Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537

                                // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 begin
                                try {
                                    var shopOb = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopDurationData.shop_id)
                                    shopDurationData.shop_lat=shopOb.shopLat.toString()
                                    shopDurationData.shop_long=shopOb.shopLong.toString()
                                    shopDurationData.shop_addr=shopOb.address.toString()
                                }catch (ex:Exception){
                                    ex.printStackTrace()
                                }
                                // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 end

                                shopDataList.add(shopDurationData)

                                //////////////////////////
                                var revisitStatusObj= ShopRevisitStatusRequestData()
                                var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                                if(data != null ){
                                    revisitStatusObj.shop_id=data.shop_id
                                    revisitStatusObj.order_status=data.order_status
                                    revisitStatusObj.order_remarks=data.order_remarks
                                    revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                                    revisitStatusList.add(revisitStatusObj)
                                }

                            }
                        }
                    }

                    uiThread {
                        if (shopDataList.isEmpty()) {
                            BaseActivity.isShopActivityUpdating = false
                            simpleDialogProcess.dismiss()
                            if(Pref.isAppInfoEnable){
                                callAppInfoApi()
                            }else{
                                (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                            }
                            //(mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                        } else {
                            val hashSet = HashSet<ShopDurationRequestData>()
                            val newShopList = ArrayList<ShopDurationRequestData>()

                            if (!Pref.isMultipleVisitEnable) {
                                for (i in shopDataList.indices) {
                                    if (hashSet.add(shopDataList[i]))
                                        newShopList.add(shopDataList[i])
                                }
                            }

                            val shopDurationApiReq = ShopDurationRequest()
                            shopDurationApiReq.user_id = Pref.user_id
                            shopDurationApiReq.session_token = Pref.session_token
                            if (newShopList.size > 0) {
                                Timber.e("Unique ShopData List size===> " + newShopList.size)
                                shopDurationApiReq.shop_list = newShopList
                            } else
                                shopDurationApiReq.shop_list = shopDataList

                            val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

                            Timber.d("callShopDurationApi : REQUEST")

                            compositeDisposable.add(
                                repository.shopDuration(shopDurationApiReq)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        Timber.d("callShopDurationApi : RESPONSE " + result.status)
                                        if (result.status == NetworkConstant.SUCCESS) {

                                            if(!revisitStatusList.isEmpty()){
                                                callRevisitStatusUploadApi(revisitStatusList!!)
                                            }
                                            if (newShopList.size > 0) {
                                                for (i in 0 until newShopList.size) {
                                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, newShopList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(newShopList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                                }
                                            } else {
                                                if (!Pref.isMultipleVisitEnable) {
                                                    for (i in 0 until shopDataList.size) {
                                                        AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                                    }
                                                }
                                                else {
                                                    for (i in 0 until shopDataList.size) {
                                                        AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!), shopDataList[i].start_timestamp!!)
                                                    }
                                                }
                                            }
                                            BaseActivity.isShopActivityUpdating = false
                                            simpleDialogProcess.dismiss()
                                            if(Pref.isAppInfoEnable){
                                                callAppInfoApi()
                                            }else{
                                                (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                            }
                                        }else{
                                            BaseActivity.isShopActivityUpdating = false
                                            simpleDialogProcess.dismiss()
                                            if(Pref.isAppInfoEnable){
                                                callAppInfoApi()
                                            }else{
                                                (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                            }
                                        }
                                        BaseActivity.isShopActivityUpdating = false
                                    }, { error ->
                                        BaseActivity.isShopActivityUpdating = false
                                        simpleDialogProcess.dismiss()
                                        if(Pref.isAppInfoEnable){
                                            callAppInfoApi()
                                        }else{
                                            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                        }
                                        if (error == null) {
                                            Timber.d("callShopDurationApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                        } else {
                                            Timber.d("callShopDurationApi : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                    })
                            )
                        }
                    }
                }
            }
        }
    }

    private fun callRevisitStatusUploadApi(revisitStatusList : MutableList<ShopRevisitStatusRequestData>){
        val revisitStatus = ShopRevisitStatusRequest()
        revisitStatus.user_id=Pref.user_id
        revisitStatus.session_token=Pref.session_token
        revisitStatus.ordernottaken_list=revisitStatusList

        val repository = ShopRevisitStatusRepositoryProvider.provideShopRevisitStatusRepository()
        compositeDisposable.add(
            repository.shopRevisitStatus(revisitStatus)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Timber.d("callRevisitStatusUploadApi : RESPONSE " + result.status)
                    if (result.status == NetworkConstant.SUCCESS){
                        for(i in revisitStatusList.indices){
                            AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.updateOrderStatus(revisitStatusList[i]!!.shop_revisit_uniqKey!!)
                        }
                    }
                },{error ->
                    if (error == null) {
                        Timber.d("callRevisitStatusUploadApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                    } else {
                        Timber.d("callRevisitStatusUploadApi : ERROR " + error.localizedMessage)
                        error.printStackTrace()
                    }
                })
        )
    }

    private fun callAppInfoApi() {
        try{
            if (Pref.isAppInfoEnable) {

                doAsync {

                    val stat = StatFs(Environment.getExternalStorageDirectory().path)
                    val totalSt = StatFs(Environment.getExternalStorageDirectory().path)
                    val bytesAvailable: Long
                    bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        stat.blockSizeLong * stat.availableBlocksLong
                    } else {
                        stat.blockSize.toLong() * stat.availableBlocks.toLong()
                    }
                    val bytesTotal: Long
                    bytesTotal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        totalSt.blockCountLong * totalSt.blockSizeLong
                    } else {
                        totalSt.blockCountLong.toLong() * totalSt.blockSizeLong.toLong()
                    }
                    val megAvailable = bytesAvailable / (1024 * 1024)
                    val megTotal = bytesTotal / (1024 * 1024)
                    println("phone_storage : FREE SPACE : " + megAvailable.toString() + " TOTAL SPACE : " + megTotal.toString() + " Time :" + AppUtils.getCurrentDateTime());
                    val batNetEntity = BatteryNetStatusEntity()
                    AppDatabase.getDBInstance()?.batteryNetDao()?.insert(batNetEntity.apply {
                        AppUtils.changeLanguage(mContext,"en")
                        bat_net_id = Pref.user_id + "_batNet_" + System.currentTimeMillis()
                        changeLocale()
                        date_time = AppUtils.getCurrentISODateTime()
                        date = AppUtils.getCurrentDateForShopActi()
                        bat_status = AppUtils.getBatteryStatus(mContext)
                        bat_level = AppUtils.getBatteryPercentage(mContext).toString()
                        net_type = AppUtils.getNetworkType(mContext)
                        mob_net_type = AppUtils.mobNetType(mContext)
                        device_model = AppUtils.getDeviceName()
                        android_version = Build.VERSION.SDK_INT.toString()
                        Available_Storage= megAvailable.toString()+"mb"
                        Total_Storage=megTotal.toString()+"mb"
                        isUploaded = false
                        Power_Saver_Status = Pref.PowerSaverStatus
                    })

                    uiThread {
                        (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                    }
                }



            }else{
                (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
            }
        }catch (ex:Exception){
            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
        }

    }

    /////////////////////////////////////////////

    private fun syncGpsNetData() {
        val unSyncData = AppDatabase.getDBInstance()?.newGpsStatusDao()?.getNotUploaded(false)
        if (unSyncData == null || unSyncData.isEmpty()){
            syncShopListOnebyOne()
        }else{
            progress_wheel.spin()
            val gps_net_status_list = ArrayList<NewGpsStatusEntity>()
            unSyncData.forEach {
                var obj :NewGpsStatusEntity = NewGpsStatusEntity()
                obj.apply {
                    id=it.id
                    date_time = it.date_time
                    gps_service_status = it.gps_service_status
                    network_status = it.network_status
                }
                gps_net_status_list.add(obj)
            }

            var sendObj : GpsNetInputModel = GpsNetInputModel()
            sendObj.user_id = Pref.user_id!!
            sendObj.session_token = Pref.session_token!!
            sendObj.gps_net_status_list = gps_net_status_list

            val repository = LocationRepoProvider.provideLocationRepository()
            compositeDisposable.add(
                repository.gpsNetInfo(sendObj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as BaseResponse
                        progress_wheel.stopSpinning()
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                unSyncData.forEach {
                                    AppDatabase.getDBInstance()?.newGpsStatusDao()?.updateIsUploadedAccordingToId(true, it.id)
                                }
                                uiThread {
                                    syncShopListOnebyOne()
                                }
                            }
                        }else{
                            syncShopListOnebyOne()
                        }
                    }, { error ->
                        if (error == null) {
                            Timber.d("App Info : ERROR : " + "UNEXPECTED ERROR IN LOCATION ACTIVITY API")
                        } else {
                            Timber.d("App Info : ERROR : " + error.localizedMessage)
                            error.printStackTrace()
                        }
                        progress_wheel.stopSpinning()
                        syncShopListOnebyOne()
                    })
            )
        }

    }

    private fun syncShopListOnebyOne() {
        dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        val progD = simpleDialogProcess.findViewById(R.id.progress_wheel_progress) as ProgressWheel
        progD.spin()
        simpleDialogProcess.show()

        val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)
        if (shopList.isEmpty() || shopList.size==0){
            //callShopDurationApiOneByOne()
            callShopDurationApi()
        }
        else{
            val addShopData = AddShopRequestData()
            val mAddShopDBModelEntity = shopList[0]
            addShopData.session_token = Pref.session_token
            addShopData.address = mAddShopDBModelEntity.address
            addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
            addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
            addShopData.owner_name = mAddShopDBModelEntity.ownerName
            addShopData.pin_code = mAddShopDBModelEntity.pinCode
            addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
            addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
            addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
            addShopData.type = mAddShopDBModelEntity.type.toString()
            addShopData.shop_id = mAddShopDBModelEntity.shop_id
            addShopData.user_id = Pref.user_id
            addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
            addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
            addShopData.added_date = mAddShopDBModelEntity.added_date
            addShopData.amount = mAddShopDBModelEntity.amount
            addShopData.area_id = mAddShopDBModelEntity.area_id
            addShopData.model_id = mAddShopDBModelEntity.model_id
            addShopData.primary_app_id = mAddShopDBModelEntity.primary_app_id
            addShopData.secondary_app_id = mAddShopDBModelEntity.secondary_app_id
            addShopData.lead_id = mAddShopDBModelEntity.lead_id
            addShopData.stage_id = mAddShopDBModelEntity.stage_id
            addShopData.funnel_stage_id = mAddShopDBModelEntity.funnel_stage_id
            addShopData.booking_amount = mAddShopDBModelEntity.booking_amount
            addShopData.type_id = mAddShopDBModelEntity.type_id

            addShopData.director_name = mAddShopDBModelEntity.director_name
            addShopData.key_person_name = mAddShopDBModelEntity.person_name
            addShopData.phone_no = mAddShopDBModelEntity.person_no

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.family_member_dob))
                addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.family_member_dob)

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_dob))
                addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_dob)

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_doa))
                addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_doa)

            addShopData.specialization = mAddShopDBModelEntity.specialization
            addShopData.category = mAddShopDBModelEntity.category
            addShopData.doc_address = mAddShopDBModelEntity.doc_address
            addShopData.doc_pincode = mAddShopDBModelEntity.doc_pincode
            addShopData.is_chamber_same_headquarter = mAddShopDBModelEntity.chamber_status.toString()
            addShopData.is_chamber_same_headquarter_remarks = mAddShopDBModelEntity.remarks
            addShopData.chemist_name = mAddShopDBModelEntity.chemist_name
            addShopData.chemist_address = mAddShopDBModelEntity.chemist_address
            addShopData.chemist_pincode = mAddShopDBModelEntity.chemist_pincode
            addShopData.assistant_contact_no = mAddShopDBModelEntity.assistant_no
            addShopData.average_patient_per_day = mAddShopDBModelEntity.patient_count
            addShopData.assistant_name = mAddShopDBModelEntity.assistant_name

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.doc_family_dob))
                addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.doc_family_dob)

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_dob))
                addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_dob)

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_doa))
                addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_doa)

            if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_family_dob))
                addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_family_dob)

            addShopData.entity_id = mAddShopDBModelEntity.entity_id
            addShopData.party_status_id = mAddShopDBModelEntity.party_status_id
            addShopData.retailer_id = mAddShopDBModelEntity.retailer_id
            addShopData.dealer_id = mAddShopDBModelEntity.dealer_id
            addShopData.beat_id = mAddShopDBModelEntity.beat_id
            addShopData.assigned_to_shop_id = mAddShopDBModelEntity.assigned_to_shop_id
            addShopData.actual_address = mAddShopDBModelEntity.actual_address

            var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id,false)
            addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!

            // duplicate shop api call
            addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate


            Handler().postDelayed(Runnable {
                callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shopList, true,
                    mAddShopDBModelEntity.doc_degree)
            }, 100)


        }
    }

    fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shopList: MutableList<AddShopDBModelEntity>?,
                       isFromInitView: Boolean, degree_imgPath: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        val index = addShop.shop_id!!.indexOf("_")
        if (shop_imgPath != null)
            Timber.d("shop image path=======> $shop_imgPath")

        if (degree_imgPath != null)
            Timber.d("doctor degree image path=======> $degree_imgPath")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(degree_imgPath)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                repository.addShop(addShop)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val addShopResult = result as AddShopResponse
                        Timber.d("syncShopFromShopList : BaseActivity " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)

                        when (addShopResult.status) {
                            NetworkConstant.SUCCESS -> {
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                syncShopListOnebyOne()
                            }
                            NetworkConstant.DUPLICATE_SHOP_ID -> {
                                Timber.d("DuplicateShop : BaseActivity " + ", SHOP: " + addShop.shop_name)
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)


                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                }
                                doAsync {
                                    uiThread {
                                        syncShopListOnebyOne()
                                    }
                                }

                            }
                            else -> {
                                (this as DashboardActivity).showSnackMessage(addShopResult.message!!)
                            }
                        }
                    }, { error ->
                        error.printStackTrace()
                        (this as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                        syncShopListOnebyOne()
                        if (error != null)
                            Timber.d("syncShopFromShopList : BaseActivity " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                    })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                repository.addShopWithImage(addShop, shop_imgPath, degree_imgPath, mContext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val addShopResult = result as AddShopResponse
                        Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)

                        when (addShopResult.status) {
                            NetworkConstant.SUCCESS -> {
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)


                                doAsync {
                                    uiThread {
                                        syncShopListOnebyOne()
                                    }
                                }
                            }
                            NetworkConstant.DUPLICATE_SHOP_ID -> {
                                Timber.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                }
                                doAsync {
                                    uiThread {
                                        syncShopListOnebyOne()
                                    }
                                }

                            }
                            else -> {
                                (this as DashboardActivity).showSnackMessage(addShopResult.message!!)
                            }
                        }
                    }, { error ->
                        error.printStackTrace()
                        (this as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                        syncShopListOnebyOne()
                        if (error != null)
                            Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                    })
            )
        }
    }

    fun callShopDurationApiOneByOne() {
        var shopId = ""
        var previousShopVisitDateNumber = 0L
        var shopVisitDate = ""

        var unsyncRevisitList = AppDatabase.getDBInstance()!!.shopActivityDao().getDurationCalculatedVisitedShopForAlDay(true,false)
        if (unsyncRevisitList.isEmpty() || unsyncRevisitList.size==0){
            callShopDurationApi()
        }else{
            Timber.e("visit_tag : callShopDurationApiOneByOne")
            BaseActivity.isShopActivityUpdating = true
            val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
            val syncedShop = ArrayList<ShopActivityEntity>()
            val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()
            doAsync {
                if (!Pref.isMultipleVisitEnable) {
                    var IsShopUploaded:Boolean = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(unsyncRevisitList[0].shopid).isUploaded
                    if(IsShopUploaded){
                        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(unsyncRevisitList[0].shopid.toString(), true, false)
                        if (shopActivity == null) {
                            val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForTodayShop(unsyncRevisitList[0].shopid.toString(),true, true,
                                AppUtils.getCurrentDateForShopActi())
                            if (shop_activity != null)
                                syncedShop.add(shop_activity)

                        }
                        else {
                            val shopDurationData = ShopDurationRequestData()
                            shopDurationData.shop_id = shopActivity.shopid
                            shopDurationData.spent_duration = shopActivity.duration_spent
                            shopDurationData.visited_date = shopActivity.visited_date
                            shopDurationData.visited_time = shopActivity.visited_date
                            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid) != null)
                                shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid).totalVisitCount
                            else
                                shopDurationData.total_visit_count = "1"

                            if (TextUtils.isEmpty(shopActivity.distance_travelled))
                                shopActivity.distance_travelled = "0.0"
                            shopDurationData.distance_travelled = shopActivity.distance_travelled

                            val currentShopVisitDateNumber = AppUtils.getTimeStampFromDateOnly(shopActivity.date!!)

                            if (shopId == shopActivity.shopid && previousShopVisitDateNumber == currentShopVisitDateNumber)
                                return@doAsync

                            shopId = shopActivity.shopid!!
                            shopVisitDate = shopActivity.date!!
                            previousShopVisitDateNumber = currentShopVisitDateNumber

                            if (!TextUtils.isEmpty(shopActivity.feedback))
                                shopDurationData.feedback = shopActivity.feedback
                            else
                                shopDurationData.feedback = ""

                            shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                            shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc

                            shopDurationData.next_visit_date = shopActivity.next_visit_date

                            if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                                shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                            else
                                shopDurationData.early_revisit_reason = ""

                            shopDurationData.device_model = shopActivity.device_model
                            shopDurationData.android_version = shopActivity.android_version
                            shopDurationData.battery = shopActivity.battery
                            shopDurationData.net_status = shopActivity.net_status
                            shopDurationData.net_type = shopActivity.net_type
                            shopDurationData.in_time = shopActivity.in_time
                            shopDurationData.out_time = shopActivity.out_time
                            shopDurationData.start_timestamp = shopActivity.startTimeStamp
                            shopDurationData.in_location = shopActivity.in_loc
                            shopDurationData.out_location = shopActivity.out_loc
                            shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

                            //duration garbage fix
                            try{
                                if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                                {
                                    shopDurationData.spent_duration="00:00:10"
                                }
                            }catch (ex:Exception){
                                shopDurationData.spent_duration="00:00:10"
                            }

                            //Begin Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537
                            if(shopActivity.isNewShop){
                                shopDurationData.isNewShop = 1
                            }else{
                                shopDurationData.isNewShop = 0
                            }
                            //End Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537

                            // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 begin
                            try {
                                var shopOb = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopDurationData.shop_id)
                                shopDurationData.shop_lat=shopOb.shopLat.toString()
                                shopDurationData.shop_long=shopOb.shopLong.toString()
                                shopDurationData.shop_addr=shopOb.address.toString()
                            }catch (ex:Exception){
                                ex.printStackTrace()
                            }
                            // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 end

                            shopDataList.add(shopDurationData)

                            //////////////////////////
                            var revisitStatusObj=ShopRevisitStatusRequestData()
                            var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                            if(data != null ){
                                revisitStatusObj.shop_id=data.shop_id
                                revisitStatusObj.order_status=data.order_status
                                revisitStatusObj.order_remarks=data.order_remarks
                                revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                                revisitStatusList.add(revisitStatusObj)
                            }
                        }

                    }
                }
                uiThread {
                    if (shopDataList.isEmpty()) {
                        callShopDurationApi()
                    }
                    else {
                        val hashSet = HashSet<ShopDurationRequestData>()
                        val newShopList = ArrayList<ShopDurationRequestData>()
                        if (!Pref.isMultipleVisitEnable) {
                            for (i in shopDataList.indices) {
                                if (hashSet.add(shopDataList[i]))
                                    newShopList.add(shopDataList[i])
                            }
                        }
                        val shopDurationApiReq = ShopDurationRequest()
                        shopDurationApiReq.user_id = Pref.user_id
                        shopDurationApiReq.session_token = Pref.session_token
                        if (newShopList.size > 0) {
                            shopDurationApiReq.shop_list = newShopList
                        } else
                            shopDurationApiReq.shop_list = shopDataList

                        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
                        compositeDisposable.add(
                            repository.shopDuration(shopDurationApiReq)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    Timber.d("callShopDurationApi : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS) {
                                        if(!revisitStatusList.isEmpty()){
                                            callRevisitStatusUploadApi(revisitStatusList!!)
                                        }
                                        if (newShopList.size > 0) {
                                            for (i in 0 until newShopList.size) {
                                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, newShopList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(newShopList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                            }
                                        } else {
                                            if (!Pref.isMultipleVisitEnable) {
                                                for (i in 0 until shopDataList.size) {
                                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                                }
                                            }
                                            else {
                                                for (i in 0 until shopDataList.size) {
                                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!), shopDataList[i].start_timestamp!!)
                                                }
                                            }
                                        }
                                    }

                                    Handler().postDelayed(Runnable {
                                        callShopDurationApiOneByOne()
                                    }, 150)
                                    BaseActivity.isShopActivityUpdating = false
                                }, { error ->
                                    callShopDurationApi()
                                    BaseActivity.isShopActivityUpdating = false
                                    if (error == null) {
                                        Timber.d("callShopDurationApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                    } else {
                                        Timber.d("callShopDurationApi : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                })
                        )
                    }

                }
            }
        }
    }

    private fun callShopActivityApiForActivityCheck() {
        Timber.d("DashboardFragment callShopActivityApiForActivityCheck started " + AppUtils.getCurrentDateTime())
        dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        val progD = simpleDialogProcess.findViewById(R.id.progress_wheel_progress) as ProgressWheel
        progD.spin()
        simpleDialogProcess.show()

        var shopActivity = ShopActivityRequest()
        shopActivity.user_id = Pref.user_id
        shopActivity.session_token = Pref.session_token
        shopActivity.date_span = "30"
        shopActivity.from_date = ""
        shopActivity.to_date = ""
        val repository = ShopActivityRepositoryProvider.provideShopActivityRepository()

        BaseActivity.compositeDisposable.add(
            repository.fetchShopActivitynew(Pref.session_token!!, Pref.user_id!!, "30", "", "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    var shopActityResponse = result as ShopActivityResponse
                    //simpleDialogProcess.dismiss()
                    Timber.d("DashboardFragment callShopActivityApiForActivityCheck response ${shopActityResponse.status}" + AppUtils.getCurrentDateTime())
                    if (shopActityResponse.status == "200") {
                        if(shopActityResponse.date_list!!.size>0){
                            var actiList = shopActityResponse.date_list as ArrayList<ShopActivityResponseDataList>
                            if(actiList!!.size>1){
                                //actiList.removeAt(actiList!!.size-1)
                                Handler().postDelayed(Runnable {
                                    updateActivityGarbage(actiList.reversed() as ArrayList<ShopActivityResponseDataList>)
                                }, 150)

                            }
                        }else{
                            endShopDuration()
                        }
                    }else{
                        endShopDuration()
                    }
                }, { error ->
                    Timber.d("DashboardFragment callShopActivityApiForActivityCheck error" + AppUtils.getCurrentDateTime())
                    simpleDialogProcess.dismiss()
                    error.printStackTrace()
                    endShopDuration()
                })
        )
    }

    fun updateActivityGarbage(listUnsync:ArrayList<ShopActivityResponseDataList>){
        doAsync {

            var dateL :ArrayList<String> = listUnsync.map { it.date } as ArrayList<String>
            var todayDatee: LocalDate = LocalDate.now()
            for(p in 0..15){
                todayDatee = AppUtils.findPrevDay(todayDatee)!!
                if(!dateL.contains(todayDatee.toString())){
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZeroByDate(false,todayDatee.toString())
                }
            }

            Timber.d("updateActivityGarbage DashFrag started " + AppUtils.getCurrentDateTime())
            for(i in 0..listUnsync.size-1){
                var shopListRoom = AppDatabase.getDBInstance()!!.shopActivityDao().getAllShopActivityByDate(listUnsync.get(i)!!.date!!.toString()) as ArrayList<String>
                var shopListApi : ArrayList<String> = listUnsync.get(i)?.shop_list!!.map { it.shopid } as ArrayList<String>
                if(shopListRoom.size > shopListApi.size){
                    var unsyncedList: List<String> = shopListRoom - shopListApi
                    for(j in 0..unsyncedList.size-1){
                        try{
                            Timber.d("updateActivityGarbage DashFrag marked unsync for  ${unsyncedList.get(j)} " + AppUtils.getCurrentDateTime())
                        }catch (ex:Exception){
                            ex.printStackTrace()
                        }
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZero(false,unsyncedList.get(j),listUnsync.get(i)!!.date!!.toString())
                    }
                }

                if(i==15){
                    break
                }

            }
            uiThread {
                //simpleDialogProcess.dismiss()
                //callShopDurationApiNew()
                Timber.d("DashboardFragment updateActivityGarbage success calling endShopDuration " + AppUtils.getCurrentDateTime())
                endShopDuration()
            }
        }
    }

    fun endShopDuration(){
        var isDurationPendingList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDurationWise(false,false)
        if(isDurationPendingList.size > 0){
            for(j in 0..isDurationPendingList.size-1){
                val endTimeStamp = System.currentTimeMillis().toString()
                val totalMinute = AppUtils.getMinuteFromTimeStamp(isDurationPendingList[j].startTimeStamp, endTimeStamp)
                val duration = AppUtils.getTimeFromTimeSpan(isDurationPendingList[j].startTimeStamp, endTimeStamp)

                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(isDurationPendingList[j].shopid!!, totalMinute, isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, isDurationPendingList[j].shopid!!, isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(isDurationPendingList[j].shopid!!, duration, isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, isDurationPendingList[j].shopid!!, isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, isDurationPendingList[j].shopid!!, isDurationPendingList[j].date!!)

                AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), isDurationPendingList[j].shopid!!, isDurationPendingList[j].date!!, isDurationPendingList[j].startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), isDurationPendingList[j].shopid!!, isDurationPendingList[j].date!!, isDurationPendingList[j].startTimeStamp)

                val netStatus = if (AppUtils.isOnline(mContext))
                    "Online"
                else
                    "Offline"

                val netType = if (AppUtils.getNetworkType(mContext).equals("wifi", ignoreCase = true))
                    AppUtils.getNetworkType(mContext)
                else
                    "Mobile ${AppUtils.mobNetType(mContext)}"

                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                    AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), isDurationPendingList[j].shopid!!,isDurationPendingList[j].date!!)
            }
            progress_wheel.stopSpinning()
            simpleDialogProcess.dismiss()
            //callShopDurationApiNew()
        }else{
            progress_wheel.stopSpinning()
            simpleDialogProcess.dismiss()
            //callShopDurationApiNew()
        }
    }

    private fun callShopDurationApiNew() {
        val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)
        if (syncedShopList.isEmpty())
            return

        progress_wheel.spin()

        BaseActivity.isShopActivityUpdating = true

        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val syncedShop = ArrayList<ShopActivityEntity>()

        val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()
        var shopIDD = ""
        var previousShopVisitDateNumber = 0L
        var shopVisitDate = ""

        doAsync {

            var counterShopList:Int = 0

            for (k in 0 until syncedShopList.size) {

                if (!Pref.isMultipleVisitEnable) {
                    /* Get shop activity that has completed time duration calculation*/
                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true, false)

                    if (shopActivity == null) {
                        val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForTodayShop(syncedShopList[k].shop_id,true, true,
                            AppUtils.getCurrentDateForShopActi())
                        if (shop_activity != null)
                            syncedShop.add(shop_activity)

                    }
                    else {
                        val shopDurationData = ShopDurationRequestData()
                        shopDurationData.shop_id = shopActivity.shopid
                        shopDurationData.spent_duration = shopActivity.duration_spent
                        shopDurationData.visited_date = shopActivity.visited_date
                        shopDurationData.visited_time = shopActivity.visited_date
                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid) != null)
                            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid).totalVisitCount
                        else
                            shopDurationData.total_visit_count = "1"

                        if (TextUtils.isEmpty(shopActivity.distance_travelled))
                            shopActivity.distance_travelled = "0.0"
                        shopDurationData.distance_travelled = shopActivity.distance_travelled

                        val currentShopVisitDateNumber = AppUtils.getTimeStampFromDateOnly(shopActivity.date!!)

                        if (shopIDD == shopActivity.shopid && previousShopVisitDateNumber == currentShopVisitDateNumber)
                            continue

                        shopIDD = shopActivity.shopid!!
                        shopVisitDate = shopActivity.date!!
                        previousShopVisitDateNumber = currentShopVisitDateNumber

                        if (!TextUtils.isEmpty(shopActivity.feedback))
                            shopDurationData.feedback = shopActivity.feedback
                        else
                            shopDurationData.feedback = ""

                        shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                        shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc

                        shopDurationData.next_visit_date = shopActivity.next_visit_date

                        if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                            shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                        else
                            shopDurationData.early_revisit_reason = ""

                        shopDurationData.device_model = shopActivity.device_model
                        shopDurationData.android_version = shopActivity.android_version
                        shopDurationData.battery = shopActivity.battery
                        shopDurationData.net_status = shopActivity.net_status
                        shopDurationData.net_type = shopActivity.net_type
                        shopDurationData.in_time = shopActivity.in_time
                        shopDurationData.out_time = shopActivity.out_time
                        shopDurationData.start_timestamp = shopActivity.startTimeStamp
                        shopDurationData.in_location = shopActivity.in_loc
                        shopDurationData.out_location = shopActivity.out_loc
                        shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

                        //duration garbage fix
                        try{
                            if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                            {
                                shopDurationData.spent_duration="00:00:10"
                            }
                        }catch (ex:Exception){
                            shopDurationData.spent_duration="00:00:10"
                        }

                        //Begin Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537
                        if(shopActivity.isNewShop){
                            shopDurationData.isNewShop = 1
                        }else{
                            shopDurationData.isNewShop = 0
                        }
                        //End Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537

                        // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 begin
                        try {
                            var shopOb = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopDurationData.shop_id)
                            shopDurationData.shop_lat=shopOb.shopLat.toString()
                            shopDurationData.shop_long=shopOb.shopLong.toString()
                            shopDurationData.shop_addr=shopOb.address.toString()
                        }catch (ex:Exception){
                            ex.printStackTrace()
                        }
                        // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 end

                        shopDataList.add(shopDurationData)

                        //////////////////////////
                        var revisitStatusObj=ShopRevisitStatusRequestData()
                        var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                        if(data != null ){
                            revisitStatusObj.shop_id=data.shop_id
                            revisitStatusObj.order_status=data.order_status
                            revisitStatusObj.order_remarks=data.order_remarks
                            revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                            revisitStatusList.add(revisitStatusObj)
                        }

                        counterShopList++
                        if(counterShopList > 300){
                            //break
                        }

                    }
                }
                else {
                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShopList(syncedShopList[k].shop_id, true,
                        false)

                    shopActivity?.forEach {
                        val shopDurationData = ShopDurationRequestData()
                        shopDurationData.shop_id = it.shopid
                        shopDurationData.spent_duration = it.duration_spent
                        shopDurationData.visited_date = it.visited_date
                        shopDurationData.visited_time = it.visited_date
                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid) != null)
                            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid).totalVisitCount
                        else
                            shopDurationData.total_visit_count = "1"

                        if (TextUtils.isEmpty(it.distance_travelled))
                            it.distance_travelled = "0.0"
                        shopDurationData.distance_travelled = it.distance_travelled

                        if (!TextUtils.isEmpty(it.feedback))
                            shopDurationData.feedback = it.feedback
                        else
                            shopDurationData.feedback = ""

                        shopDurationData.isFirstShopVisited = it.isFirstShopVisited
                        shopDurationData.distanceFromHomeLoc = it.distance_from_home_loc

                        shopDurationData.next_visit_date = it.next_visit_date

                        if (!TextUtils.isEmpty(it.early_revisit_reason))
                            shopDurationData.early_revisit_reason = it.early_revisit_reason
                        else
                            shopDurationData.early_revisit_reason = ""

                        shopDurationData.device_model = it.device_model
                        shopDurationData.android_version = it.android_version
                        shopDurationData.battery = it.battery
                        shopDurationData.net_status = it.net_status
                        shopDurationData.net_type = it.net_type
                        shopDurationData.in_time = it.in_time
                        shopDurationData.out_time = it.out_time
                        shopDurationData.start_timestamp = it.startTimeStamp
                        shopDurationData.in_location = it.in_loc
                        shopDurationData.out_location = it.out_loc
                        shopDurationData.shop_revisit_uniqKey=it.shop_revisit_uniqKey!!

                        //duration garbage fix
                        try{
                            if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                            {
                                shopDurationData.spent_duration="00:00:10"
                            }
                        }catch (ex:Exception){
                            shopDurationData.spent_duration="00:00:10"
                        }

                        //Begin Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537
                        if(it.isNewShop){
                            shopDurationData.isNewShop = 1
                        }else{
                            shopDurationData.isNewShop = 0
                        }
                        //End Rev 1.0 Suman 10-07-2023 IsnewShop in api+room mantis id 26537

                        // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 begin
                        try {
                            var shopOb = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopDurationData.shop_id)
                            shopDurationData.shop_lat=shopOb.shopLat.toString()
                            shopDurationData.shop_long=shopOb.shopLong.toString()
                            shopDurationData.shop_addr=shopOb.address.toString()
                        }catch (ex:Exception){
                            ex.printStackTrace()
                        }
                        // Rev 2.0 Suman 06-05-2024 Suman DashboardFragment mantis 27335 end

                        shopDataList.add(shopDurationData)

                        //////////////////////////
                        var revisitStatusObj=ShopRevisitStatusRequestData()
                        var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                        if(data != null ){
                            revisitStatusObj.shop_id=data.shop_id
                            revisitStatusObj.order_status=data.order_status
                            revisitStatusObj.order_remarks=data.order_remarks
                            revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                            revisitStatusList.add(revisitStatusObj)
                        }

                    }
                }
            }
            uiThread {
                if (shopDataList.isEmpty()) {
                    BaseActivity.isShopActivityUpdating = false
                    progress_wheel.stopSpinning()
                    simpleDialogProcess.dismiss()
                }
                else {
                    val hashSet = HashSet<ShopDurationRequestData>()
                    val newShopList = ArrayList<ShopDurationRequestData>()

                    if (!Pref.isMultipleVisitEnable) {
                        for (i in shopDataList.indices) {
                            if (hashSet.add(shopDataList[i]))
                                newShopList.add(shopDataList[i])
                        }
                    }

                    val shopDurationApiReq = ShopDurationRequest()
                    shopDurationApiReq.user_id = Pref.user_id
                    shopDurationApiReq.session_token = Pref.session_token
                    if (newShopList.size > 0) {
                        Timber.e("Unique ShopData List size===> " + newShopList.size)
                        shopDurationApiReq.shop_list = newShopList
                    } else
                        shopDurationApiReq.shop_list = shopDataList

                    val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

                    compositeDisposable.add(
                        repository.shopDuration(shopDurationApiReq)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                if (result.status == NetworkConstant.SUCCESS) {
                                    if(!revisitStatusList.isEmpty()){
                                        callRevisitStatusUploadApi(revisitStatusList!!)
                                    }
                                    if (newShopList.size > 0) {
                                        for (i in 0 until newShopList.size) {
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, newShopList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(newShopList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                        }
                                    } else {
                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in 0 until shopDataList.size) {
                                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                            }
                                        }
                                        else {
                                            for (i in 0 until shopDataList.size) {
                                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!), shopDataList[i].start_timestamp!!)
                                            }
                                        }
                                    }
                                }
                                BaseActivity.isShopActivityUpdating = false
                                progress_wheel.stopSpinning()
                                simpleDialogProcess.dismiss()
                            }, { error ->
                                BaseActivity.isShopActivityUpdating = false
                                progress_wheel.stopSpinning()
                                simpleDialogProcess.dismiss()
                                if (error == null) {
                                    Timber.d("callShopDurationApii : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                } else {
                                    Timber.d("callShopDurationApii : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                            })
                    )
                }

            }
        }
    }


}
