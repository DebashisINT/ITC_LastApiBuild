package com.breezedsm.features.averageshop.presentation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import timber.log.Timber
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.google.gson.Gson
import com.pnikosis.materialishprogress.ProgressWheel
import com.breezedsm.R
import com.breezedsm.app.AppDatabase
import com.breezedsm.app.NetworkConstant
import com.breezedsm.app.Pref
import com.breezedsm.app.domain.*
import com.breezedsm.app.types.FragType
import com.breezedsm.app.utils.AppUtils
import com.breezedsm.app.utils.Toaster
import com.breezedsm.base.BaseResponse
import com.breezedsm.base.presentation.BaseActivity
import com.breezedsm.base.presentation.BaseActivity.Companion.compositeDisposable
import com.breezedsm.base.presentation.BaseActivity.Companion.isShopActivityUpdating
import com.breezedsm.base.presentation.BaseFragment
import com.breezedsm.features.addshop.api.AddShopRepositoryProvider
import com.breezedsm.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.breezedsm.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.breezedsm.features.addshop.api.typeList.TypeListRepoProvider
import com.breezedsm.features.addshop.model.AddShopRequestCompetetorImg
import com.breezedsm.features.addshop.model.AddShopRequestData
import com.breezedsm.features.addshop.model.AddShopResponse
import com.breezedsm.features.addshop.model.AssignedToShopListResponseModel
import com.breezedsm.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.breezedsm.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.breezedsm.features.averageshop.api.ShopActivityRepositoryProvider
import com.breezedsm.features.averageshop.business.InfoWizard
import com.breezedsm.features.averageshop.model.ShopActivityRequest
import com.breezedsm.features.averageshop.model.ShopActivityResponse
import com.breezedsm.features.averageshop.model.ShopActivityResponseDataList
import com.breezedsm.features.dashboard.presentation.DashboardActivity
import com.breezedsm.features.dashboard.presentation.api.ShopVisitImageUploadRepoProvider
import com.breezedsm.features.dashboard.presentation.model.ShopVisitImageUploadInputModel
import com.breezedsm.features.location.LocationWizard
import com.breezedsm.features.location.model.ShopDurationRequest
import com.breezedsm.features.location.model.ShopDurationRequestData
import com.breezedsm.features.location.model.ShopRevisitStatusRequest
import com.breezedsm.features.location.model.ShopRevisitStatusRequestData
import com.breezedsm.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.breezedsm.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.breezedsm.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Pratishruti on 15-11-2017.
 */
class AverageShopFragment : BaseFragment(), DatePickerListener, View.OnClickListener {

    lateinit var averageShopListAdapter: AverageShopListAdapter
    private lateinit var shopList: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noShopAvailable: AppCompatTextView
    private lateinit var picker: HorizontalPicker
    private lateinit var ShopActivityEntityList: List<ShopActivityEntity>
    private lateinit var noOfShop: AppCustomTextView
    private lateinit var avg_shop_tv: TextView
    private lateinit var total_shop_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var selectedDate: String
    private lateinit var sync_all_tv: AppCustomTextView
    private lateinit var sync_by_date_tv: AppCustomTextView
    private lateinit var tv_frag_avg_shop_total_visit_count: TextView
    var i: Int = 0
    private var j: Int = 0


    val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()

    lateinit var simpleDialogProcess : Dialog
    lateinit var dialogHeaderProcess: AppCustomTextView
    lateinit var dialog_yes_no_headerTVProcess: AppCustomTextView

    private lateinit var mContext: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_average_shop_visit, container, false)
        selectedDate = AppUtils.getCurrentDateForShopActi()
        initView(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {

        simpleDialogProcess = Dialog(mContext)
        simpleDialogProcess.setCancelable(false)
        simpleDialogProcess.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialogProcess.setContentView(R.layout.dialog_message_progress)
        dialogHeaderProcess = simpleDialogProcess.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        dialog_yes_no_headerTVProcess = simpleDialogProcess.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView

        /*NEW CALENDER*/
        picker = view.findViewById<HorizontalPicker>(R.id.datePicker)
        picker.setListener(this)
                .setDays(60)
                .setOffset(44)
                .setDateSelectedColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//box color
                .setDateSelectedTextColor(ContextCompat.getColor(mContext, R.color.white))
                .setMonthAndYearTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//month color
                .setTodayButtonTextColor(ContextCompat.getColor(mContext, R.color.date_selector_color))
                .setTodayDateTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setTodayDateBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent))//
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setDayOfWeekTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .showTodayButton(false)
                .init()

        Handler().postDelayed(Runnable {
            picker.backgroundColor = Color.WHITE
            picker.setDate(DateTime())
            var dt = DateTime().toLocalDateTime()
            println("tag_dt $dt")
        }, 500)



        /*NEW CALENDER*/

        noShopAvailable = view.findViewById(R.id.no_shop_tv)
        shopList = view.findViewById(R.id.shop_list_RCV)
        noOfShop = view.findViewById(R.id.no_of_shop_TV)
        avg_shop_tv = view.findViewById(R.id.avg_shop_tv)
        total_shop_TV = view.findViewById(R.id.total_shop_TV)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        sync_all_tv = view.findViewById(R.id.sync_all_tv);
        sync_by_date_tv = view.findViewById(R.id.sync_by_date_tv);
        tv_frag_avg_shop_total_visit_count = view.findViewById(R.id.tv_frag_avg_shop_total_visit_count);
        progress_wheel.stopSpinning()
        total_shop_TV.text = InfoWizard.getTotalShopVisitCount()
        noOfShop.text = InfoWizard.getAvergareShopVisitCount()
        sync_all_tv.setOnClickListener(this)
        sync_by_date_tv.setOnClickListener(this)


        /*if (AppDatabase.getDBInstance()!!.shopActivityDao().getAll().isEmpty()) {
            Handler().postDelayed(Runnable {
                Timber.d("DashFrag callShopActivityApi started ${AppUtils.getCurrentDateTime()}")
                //callShopActivityApi()
            }, 100)
        }else{
            initShopList()
        }*/


        sync_by_date_tv.text = "Sync for $selectedDate"

        Handler().postDelayed(Runnable {
            initShopList()
        }, 2000)
    }

    private fun callShopActivityApi() {
        var shopActivity = ShopActivityRequest()
        shopActivity.user_id = Pref.user_id
        shopActivity.session_token = Pref.session_token
        shopActivity.date_span = "30"
        shopActivity.from_date = ""
        shopActivity.to_date = ""
        val repository = ShopActivityRepositoryProvider.provideShopActivityRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.fetchShopActivitynew(Pref.session_token!!, Pref.user_id!!, "30", "", "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                        var shopActityResponse = result as ShopActivityResponse
                    progress_wheel.stopSpinning()
                        Timber.d("AverageShopFrag callShopActivityApi ${shopActityResponse.status} ${AppUtils.getCurrentDateTime()}")
                        if (shopActityResponse.status == "200") {
                            updateShopTableInDB(shopActityResponse.date_list)
                        }else{
                            initShopList()
                        }
                           }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    initShopList()
                })
        )
    }

    private fun updateShopTableInDB(date_list: List<ShopActivityResponseDataList>?) {

        progress_wheel.spin()

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
                progress_wheel.stopSpinning()
                Handler().postDelayed(Runnable {
                    Timber.d("DashFrag callShopActivityApi started ${AppUtils.getCurrentDateTime()}")
                    initShopList()
                }, 2000)
                Timber.d("AverageShopFrag callShopActivityApi updateShopTableInDB finished ${AppUtils.getCurrentDateTime()}")
            }
        }



    }

    override fun onClick(p0: View?) {
        i = 0
        when (p0?.id) {
            R.id.sync_by_date_tv -> {
                if(!isShopActivityUpdating){
                    if(AppUtils.isOnline(mContext)){
                        var selD = selectedDate
                        callShopActivityApiForActivityCheckDateWise(selD,selD)
                    }
                }
            }
            R.id.sync_all_tv -> {
                /*if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty())
                    syncAllShopActivity(ShopActivityEntityList[i].shopid!!)
                else {
                    var unSyncedList: List<ShopVisitImageModelEntity>? = null
                    for (i in ShopActivityEntityList.indices) {
                        unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                    }

                    if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                        j = 0
                        callShopVisitImageUploadApiForAll(unSyncedList)
                    }
                }*/

                //AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZeroByDate(false,"2022-09-28")
                //AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZeroByDate(false,"2022-09-30")

                if(!isShopActivityUpdating){
                    if(AppUtils.isOnline(mContext)){
                    callShopActivityApiForActivityCheck()
                        //callShopDurationApiNew()
                    }
                }
                //callShopDurationApiNew()
            /*
                Handler().postDelayed(Runnable {
                    if (!Pref.isMultipleVisitEnable) {
                        if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty()) {

                            var list = ArrayList<ShopActivityEntity>()

                            for (i in ShopActivityEntityList.indices) {
                                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                                if (shop.isUploaded) {
                                    if (ShopActivityEntityList[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                                        if (AppUtils.isVisitSync == "1")
                                            list.add(ShopActivityEntityList[i])
                                        else {
                                            if (!ShopActivityEntityList[i].isUploaded)
                                                list.add(ShopActivityEntityList[i])
                                        }
                                    }
                                }
                            }

                            /*if(list.size>0){
                                for( i in list?.indices){
                                    var revisitStatusObj= ShopRevisitStatusRequestData()
                                    var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(list?.get(i).shop_revisit_uniqKey.toString())
                                    if(data != null ){
                                        revisitStatusObj.shop_id=data.shop_id
                                        revisitStatusObj.order_status=data.order_status
                                        revisitStatusObj.order_remarks=data.order_remarks
                                        revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                                        revisitStatusList.add(revisitStatusObj)
                                    }
                                }
                            }*/


                            list = list.filter { it.isUploaded == false } as ArrayList<ShopActivityEntity>

                            if (list.size > 0)
                                syncAllShopActivity(list[i].shopid!!, list)
                            else
                                syncShopVisitImage()

                        } else {
                            syncShopVisitImage()
                        }
                    }
                    else {
                        if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty()) {

                            var list = ArrayList<ShopActivityEntity>()

                            for (i in ShopActivityEntityList.indices) {
                                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                                if (shop.isUploaded) {
                                    if (ShopActivityEntityList[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                                        if (AppUtils.isVisitSync == "1")
                                            list.add(ShopActivityEntityList[i])
                                        else {
                                            if (!ShopActivityEntityList[i].isUploaded)
                                                list.add(ShopActivityEntityList[i])
                                        }
                                    }
                                }
                            }

                            list = list.filter { it.isUploaded == false } as ArrayList<ShopActivityEntity>

                            if (list.size > 0)
                                syncAllShopActivityForMultiVisit(list)
                        }
                    }
                }, 5700)

*/
            }
        }
    }


    private fun callShopActivityApiForActivityCheck() {
        dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        val progD = simpleDialogProcess.findViewById(R.id.progress_wheel_progress) as ProgressWheel
        progD.spin()
        simpleDialogProcess.show()


        progress_wheel.spin()
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
                    progress_wheel.stopSpinning()
                    if (shopActityResponse.status == "200") {
                        if(shopActityResponse.date_list!!.size>0){
                            var actiList = shopActityResponse.date_list as ArrayList<ShopActivityResponseDataList>
                            if(actiList!!.size>1){
                                Handler().postDelayed(Runnable {
                                    updateActivityGarbage(actiList.reversed() as java.util.ArrayList<ShopActivityResponseDataList>,false)
                                }, 150)
                            }
                        }
                    }else{
                        //callShopDurationApiNew()
                        endShopDuration()
                    }
                }, { error ->
                    simpleDialogProcess.dismiss()
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    //callShopDurationApiNew()
                    endShopDuration()
                })
        )
    }

    private fun callShopActivityApiForActivityCheckDateWise(selected_fromDate:String,selected_toDate:String) {
        dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        val progD = simpleDialogProcess.findViewById(R.id.progress_wheel_progress) as ProgressWheel
        progD.spin()
        simpleDialogProcess.show()

        progress_wheel.spin()
        var shopActivity = ShopActivityRequest()
        shopActivity.user_id = Pref.user_id
        shopActivity.session_token = Pref.session_token
        shopActivity.date_span = "30"
        shopActivity.from_date = ""
        shopActivity.to_date = ""
        val repository = ShopActivityRepositoryProvider.provideShopActivityRepository()

        BaseActivity.compositeDisposable.add(
            repository.fetchShopActivitynew(Pref.session_token!!, Pref.user_id!!, "", selected_fromDate, selected_toDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    var shopActityResponse = result as ShopActivityResponse
                    progress_wheel.stopSpinning()
                    if (shopActityResponse.status == "200") {
                        if(shopActityResponse.date_list!!.size>0){
                            var actiList = shopActityResponse.date_list as ArrayList<ShopActivityResponseDataList>

                            if(actiList!!.size > 0){
                                Handler().postDelayed(Runnable {
                                    updateActivityGarbage(actiList,true)
                                }, 150)
                            }
                        }else{
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZeroByDate(false,selected_fromDate)
                            endShopDuration()
                        }
                    }else{
                        endShopDuration()
                    }
                }, { error ->
                    simpleDialogProcess.dismiss()
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    //callShopDurationApiNew()
                    endShopDuration()
                })
        )
    }

    fun updateActivityGarbage(listUnsync:ArrayList<ShopActivityResponseDataList>,isDayeWise:Boolean){
        progress_wheel.spin()

        doAsync {

            var dateL :ArrayList<String> = listUnsync.map { it.date } as ArrayList<String>

            if(!dateL.contains(AppUtils.getCurrentDateForShopActi()) && isDayeWise == false){
                AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZeroByDate(false,AppUtils.getCurrentDateForShopActi())
            }

            if(isDayeWise == false){
            var todayDate: LocalDate = LocalDate.now()
            for(p in 0..15){
                todayDate = AppUtils.findPrevDay(todayDate)!!
                if(!dateL.contains(todayDate.toString())){
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateShopForIsuploadZeroByDate(false,todayDate.toString())
                }
            }
            }

            for(i in 0..listUnsync.size-1){
                var shopListRoom = AppDatabase.getDBInstance()!!.shopActivityDao().getAllShopActivityByDate(listUnsync.get(i)!!.date!!.toString()) as ArrayList<String>
                //var shopListApi : ArrayList<String> = listUnsync.get(i)?.shop_list!!.map { it.shopid } as ArrayList<String>

                var shopListApi : ArrayList<String> = ArrayList()
                for(j in 0..listUnsync.get(i)?.shop_list!!.size-1){
                    shopListApi.add(listUnsync.get(i)?.shop_list!!.get(j).shopid!!)
                }

                if(shopListRoom.size > shopListApi.size){
                    var unsyncedList: List<String> = shopListRoom - shopListApi
                    for(j in 0..unsyncedList.size-1){
                        try{
                            Timber.d("updateActivityGarbage averageshopfrag marked unsync for  ${unsyncedList.get(j)}" + AppUtils.getCurrentDateTime())
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
                progress_wheel.stopSpinning()
                //callShopDurationApiNew()
                //syncShopListOnebyOne()
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

                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(isDurationPendingList[j].shopid!!, totalMinute,  isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, isDurationPendingList[j].shopid!!,  isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(isDurationPendingList[j].shopid!!, duration,  isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, isDurationPendingList[j].shopid!!,  isDurationPendingList[j].date!!)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, isDurationPendingList[j].shopid!!,  isDurationPendingList[j].date!!)

                AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), isDurationPendingList[j].shopid!!,  isDurationPendingList[j].date!!, isDurationPendingList[j].startTimeStamp)
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

            syncShopListOnebyOne()
        }else{
            syncShopListOnebyOne()
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
            callShopDurationApiNew()
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


    private fun syncAllShopActivityForMultiVisit(list_: ArrayList<ShopActivityEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token

        for (i in list_.indices) {
            val shopActivity = list_[i]

            val shopDurationData = ShopDurationRequestData()
            shopDurationData.shop_id = shopActivity.shopid
            if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                if (!Pref.isMultipleVisitEnable) {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                } else {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                }

                shopDurationData.spent_duration = duration
            } else {
                shopDurationData.spent_duration = shopActivity.duration_spent
            }
            shopDurationData.visited_date = shopActivity.visited_date
            shopDurationData.visited_time = shopActivity.visited_date
            if (TextUtils.isEmpty(shopActivity.distance_travelled))
                shopActivity.distance_travelled = "0.0"
            shopDurationData.distance_travelled = shopActivity.distance_travelled
            val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
            if (list != null && list.isNotEmpty())
                shopDurationData.total_visit_count = list[0].totalVisitCount

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
            shopDurationData.shop_revisit_uniqKey=shopActivity.shop_revisit_uniqKey

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

            shopDataList.add(shopDurationData)

            Timber.d("========SYNC ALL VISITED SHOP DATA (AVERAGE SHOP)=====")
            Timber.d("SHOP ID======> " + shopDurationData.shop_id)
            Timber.d("SPENT DURATION======> " + shopDurationData.spent_duration)
            Timber.d("VISIT DATE=========> " + shopDurationData.visited_date)
            Timber.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
            Timber.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
            Timber.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
            Timber.d("FEEDBACK========> " + shopDurationData.feedback)
            Timber.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
            Timber.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
            Timber.d("next_visit_date========> " + shopDurationData.next_visit_date)
            Timber.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
            Timber.d("device_model========> " + shopDurationData.device_model)
            Timber.d("android_version========> " + shopDurationData.android_version)
            Timber.d("battery========> " + shopDurationData.battery)
            Timber.d("net_status========> " + shopDurationData.net_status)
            Timber.d("net_type========> " + shopDurationData.net_type)
            Timber.d("in_time========> " + shopDurationData.in_time)
            Timber.d("out_time========> " + shopDurationData.out_time)
            Timber.d("start_timestamp========> " + shopDurationData.start_timestamp)
            Timber.d("in_location========> " + shopDurationData.in_location)
            Timber.d("out_location========> " + shopDurationData.out_location)
            Timber.d("=======================================================")
        }

        if (shopDataList.isEmpty()) {
            return
        }

        Log.e("Average Shop", "isShopActivityUpdating====> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true
/////////////
        revisitStatusList.clear()
        for(i in 0..shopDataList?.size-1){
            var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDataList?.get(i)?.shop_revisit_uniqKey!!.toString())
            if(data!=null ){
                var revisitStatusObj= ShopRevisitStatusRequestData()
                revisitStatusObj.shop_id=data.shop_id
                revisitStatusObj.order_status=data.order_status
                revisitStatusObj.order_remarks=data.order_remarks
                revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                revisitStatusList.add(revisitStatusObj)
            }
        }

/////////////////
        progress_wheel.spin()
        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            Timber.d("ShopActivityFromAverageShop : RESPONSE STATUS:= " + result.status + ", RESPONSE MESSAGE:= " + result.message +
                                    "\nUser Id" + Pref.user_id + ", Session Token" + Pref.session_token)
                            if (result.status == NetworkConstant.SUCCESS) {
                                shopDataList.forEach {
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, it.shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(it.visited_date!!), it.start_timestamp!!)
                                }


                                if(!revisitStatusList.isEmpty()){
                                    callRevisitStatusUploadApi(revisitStatusList!!)
                                }
                                for(i in 0..shopDataList?.size-1){
                                    callCompetetorImgUploadApi(shopDataList?.get(i)?.shop_id!!)
                                }

                                val dateWiseList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                Timber.d("=======UPDATE ADAPTER FOR SYNC ALL VISIT SHOP DATA (AVERAGE SHOP)=======")
                                Timber.d("shop list size====> " + dateWiseList.size)
                                Timber.d("specific date====> $selectedDate")

                                averageShopListAdapter.updateList(dateWiseList)

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                Collections.reverse(ShopActivityEntityList)

                                progress_wheel.stopSpinning()
                                BaseActivity.isShopActivityUpdating = false
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                BaseActivity.isShopActivityUpdating = false
                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            if (error != null) {
                                Timber.d("ShopActivityFromAverageShop : ERROR:= " + error.localizedMessage + "\nUser Id" + Pref.user_id +
                                        ", Session Token" + Pref.session_token)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }
                        })
        )

    }


    private fun syncShopVisitImage() {

        progress_wheel.spin()

        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
        if (ShopActivityEntityList != null) {
            for (i in ShopActivityEntityList.indices) {
                /*val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                if (shop.isUploaded) {*/
                if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                    var unSyncedData: List<ShopVisitImageModelEntity>? = null

                    /*val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                            ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)*/

                    if (AppUtils.isVisitSync == "1")
                        unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                    else
                        unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                                ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                    if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                        unSyncedList.add(unSyncedData[0])
                    }
                }
                //}
            }

            if (unSyncedList.size > 0) {
                j = 0
                callShopVisitImageUploadApiForAll(unSyncedList)
            } else {
                val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                for (i in ShopActivityEntityList.indices) {
                    if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                        var unSyncedData: List<ShopVisitAudioEntity>? = null

                        unSyncedData = if (AppUtils.isVisitSync == "1")
                            AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                        else
                            AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                    ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                        if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                            unSyncedAudioList.add(unSyncedData[0])
                        }
                    }
                }

                if (unSyncedAudioList.isNotEmpty()) {
                    j = 0
                    callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                }
            }
        }

        Handler().postDelayed(Runnable {
            progress_wheel.stopSpinning()
            initShopList()
        }, 1500)

    }

    private fun callShopVisitImageUploadApiForAll(unSyncedList: List<ShopVisitImageModelEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val visitImageShop = ShopVisitImageUploadInputModel()
        visitImageShop.session_token = Pref.session_token
        visitImageShop.user_id = Pref.user_id
        visitImageShop.shop_id = unSyncedList[j].shop_id
        visitImageShop.visit_datetime = unSyncedList[j].visit_datetime

        Log.e("Average Shop", "isShopActivityUpdating=============> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        Timber.d("========UPLOAD REVISIT ALL IMAGE INPUT PARAMS (AVERAGE SHOP)======")
        Timber.d("USER ID======> " + visitImageShop.user_id)
        Timber.d("SESSION ID======> " + visitImageShop.session_token)
        Timber.d("SHOP ID=========> " + visitImageShop.shop_id)
        Timber.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
        Timber.d("IMAGE========> " + unSyncedList[j].shop_image)
        Timber.d("=====================================================================")

        val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.visitShopWithImage(visitImageShop, unSyncedList[j].shop_image!!, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val logoutResponse = result as BaseResponse
                            Timber.d("UPLOAD REVISIT ALL IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, unSyncedList.get(j).shop_id!!)

                                j++
                                if (j < unSyncedList.size) {
                                    BaseActivity.isShopActivityUpdating = false
                                    callShopVisitImageUploadApiForAll(unSyncedList)
                                } else {
                                    j = 0
                                    BaseActivity.isShopActivityUpdating = false

                                    //callShopDurationApi()

                                    val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                                    for (i in ShopActivityEntityList.indices) {
                                        if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                                            var unSyncedData: List<ShopVisitAudioEntity>? = null

                                            unSyncedData = if (AppUtils.isVisitSync == "1")
                                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                                            else
                                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                                        ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                                            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                unSyncedAudioList.add(unSyncedData[0])
                                            }
                                        }
                                    }

                                    if (unSyncedAudioList.isNotEmpty()) {
                                        progress_wheel.stopSpinning()
                                        j = 0
                                        callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                                    } else {
                                        (mContext as DashboardActivity).showSnackMessage("Sync Successful")
                                        progress_wheel.stopSpinning()

                                        val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                        Timber.d("=======UPDATE ADAPTER FOR SYNC ALL IMAGE (AVERAGE SHOP)=======")
                                        Timber.d("shop list size====> " + list.size)
                                        Timber.d("specific date====> $selectedDate")

                                        averageShopListAdapter.updateList(list)
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                BaseActivity.isShopActivityUpdating = false
                                (mContext as DashboardActivity).showSnackMessage(logoutResponse.message!!)
                            }
                        }, { error ->
                            Timber.d("UPLOAD REVISIT ALL IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                        })
        )
    }

    private fun callShopVisitAudioUploadApiForAll(unSyncedList: List<ShopVisitAudioEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val visitImageShop = ShopVisitImageUploadInputModel()
        visitImageShop.session_token = Pref.session_token
        visitImageShop.user_id = Pref.user_id
        visitImageShop.shop_id = unSyncedList[j].shop_id
        visitImageShop.visit_datetime = unSyncedList[j].visit_datetime

        Log.e("Average Shop", "isShopActivityUpdating=============> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        Timber.d("========UPLOAD REVISIT ALL AUDIO INPUT PARAMS (AVERAGE SHOP)======")
        Timber.d("USER ID======> " + visitImageShop.user_id)
        Timber.d("SESSION ID======> " + visitImageShop.session_token)
        Timber.d("SHOP ID=========> " + visitImageShop.shop_id)
        Timber.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
        Timber.d("AUDIO========> " + unSyncedList[j].audio)
        Timber.d("=====================================================================")

        val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.visitShopWithAudio(visitImageShop, unSyncedList[j].audio!!, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val logoutResponse = result as BaseResponse
                            Timber.d("UPLOAD REVISIT ALL AUDIO : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedList.get(j).shop_id!!)

                                j++
                                if (j < unSyncedList.size) {
                                    progress_wheel.stopSpinning()
                                    BaseActivity.isShopActivityUpdating = false
                                    callShopVisitAudioUploadApiForAll(unSyncedList)
                                } else {
                                    j = 0
                                    BaseActivity.isShopActivityUpdating = false
                                    (mContext as DashboardActivity).showSnackMessage("Sync Successful")
                                    progress_wheel.stopSpinning()

                                    val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                    Timber.d("=======UPDATE ADAPTER FOR SYNC ALL AUDIO (AVERAGE SHOP)=======")
                                    Timber.d("shop list size====> " + list.size)
                                    Timber.d("specific date====> $selectedDate")

                                    averageShopListAdapter.updateList(list)
                                    //callShopDurationApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                BaseActivity.isShopActivityUpdating = false
                                (mContext as DashboardActivity).showSnackMessage(logoutResponse.message!!)
                            }
                        }, { error ->
                            Timber.d("UPLOAD REVISIT ALL AUDIO : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                        })
        )
    }


    override fun onDateSelected(dateSelected: DateTime) {
        var dateTime = dateSelected.toString()
        var dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        selectedDate = dateFormat
        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(dateFormat)

        sync_by_date_tv.text = "Sync for $selectedDate"

        Collections.reverse(ShopActivityEntityList)

        if (ShopActivityEntityList.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE
            shopList.visibility = View.VISIBLE

            Timber.d("===========INIT ADAPTER FOR SPECIFIC DATE (AVERAGE SHOP)========")
            Timber.d("shop list size====> " + ShopActivityEntityList.size)
            Timber.d("specific date====> $selectedDate")

            try {
                initAdapter()
            } catch (e: UninitializedPropertyAccessException) {
                initAdapter()
            }

        } else {
            tv_frag_avg_shop_total_visit_count.text="Total Visit (Count) : ${ShopActivityEntityList.size}"
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }

    }

    private fun syncShopActivity(shopId: String) {


        try {

            if (!AppUtils.isOnline(mContext)) {
                isShopActivityUpdating = false
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }
            val mList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, selectedDate)
            if (mList.isEmpty()){
                isShopActivityUpdating = false
                return
            }
            val shopActivity = mList[0]
//        var shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)
            val shopDurationApiReq = ShopDurationRequest()
            shopDurationApiReq.user_id = Pref.user_id
            shopDurationApiReq.session_token = Pref.session_token
            val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
            val shopDurationData = ShopDurationRequestData()
            shopDurationData.shop_id = shopActivity.shopid
            if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                if (!Pref.isMultipleVisitEnable) {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                }
                else {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                }

                shopDurationData.spent_duration = duration
            } else {
                shopDurationData.spent_duration = shopActivity.duration_spent
            }
            shopDurationData.visited_date = shopActivity.visited_date
            shopDurationData.visited_time = shopActivity.visited_date
            if (TextUtils.isEmpty(shopActivity.distance_travelled))
                shopActivity.distance_travelled = "0.0"
            shopDurationData.distance_travelled = shopActivity.distance_travelled
            val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
            if (list != null && list.isNotEmpty())
                shopDurationData.total_visit_count = list[0].totalVisitCount

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

            shopDataList.add(shopDurationData)

            if (shopDataList.isEmpty()) {
                isShopActivityUpdating = false
                return
            }

            Timber.d("===========SYNC VISITED SHOP DATA (AVERAGE SHOP)========")
            Timber.d("SHOP ID======> " + shopDurationData.shop_id)
            Timber.d("SPENT DURATION======> " + shopDurationData.spent_duration)
            Timber.d("VISIT DATE=========> " + shopDurationData.visited_date)
            Timber.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
            Timber.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
            Timber.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
            Timber.d("FEEDBACK========> " + shopDurationData.feedback)
            Timber.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
            Timber.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
            Timber.d("next_visit_date========> " + shopDurationData.next_visit_date)
            Timber.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
            Timber.d("device_model========> " + shopDurationData.device_model)
            Timber.d("android_version========> " + shopDurationData.android_version)
            Timber.d("battery========> " + shopDurationData.battery)
            Timber.d("net_status========> " + shopDurationData.net_status)
            Timber.d("net_type========> " + shopDurationData.net_type)
            Timber.d("in_time========> " + shopDurationData.in_time)
            Timber.d("out_time========> " + shopDurationData.out_time)
            Timber.d("start_timestamp========> " + shopDurationData.start_timestamp)
            Timber.d("in_location========> " + shopDurationData.in_location)
            Timber.d("out_location========> " + shopDurationData.out_location)
            Timber.d("===========================================================")

            progress_wheel.spin()
            shopDurationApiReq.shop_list = shopDataList
            val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
            var gson = Gson();
            var jsonInString = gson.toJson(shopDurationApiReq);
            Log.v("TAG", jsonInString)
            BaseActivity.compositeDisposable.add(
                    repository.shopDuration(shopDurationApiReq)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->

                                Timber.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", RESPONSE:" + result.message)
                                if (result.status == NetworkConstant.SUCCESS) {

                                    doAsync {

                                        if (!Pref.isMultipleVisitEnable)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate)
                                        else
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate, shopActivity.startTimeStamp)

                                        /*var unSyncedList: List<ShopVisitImageModelEntity>? = null
                                    for (i in shopDataList.indices) {
                                        unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)
                                    }*/


                                        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()

                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in shopDataList.indices) {
                                                val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

                                                if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                    unSyncedList.add(unSyncedData[0])
                                                }
                                            }
                                        }

                                        uiThread {
                                            isShopActivityUpdating = false
                                            progress_wheel.stopSpinning()

                                            if (unSyncedList.size > 0) {
                                                callShopVisitImageUploadApi(unSyncedList, false, null)
                                            } else {
                                                val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()

                                                if (!Pref.isMultipleVisitEnable) {
                                                    for (i in shopDataList.indices) {
                                                        val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

                                                        if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                            unSyncedAudioList.add(unSyncedData[0])
                                                        }
                                                    }
                                                }

                                                if (unSyncedAudioList.isNotEmpty())
                                                    callShopVisitAudioUploadApi(unSyncedAudioList, false, null)
                                                else {
                                                    (mContext as DashboardActivity).showSnackMessage("Sync successful")
                                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                                    Collections.reverse(ShopActivityEntityList)
                                                    averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                                }
                                            }
                                        }
                                    }

                                }
                                else {
                                    isShopActivityUpdating = false
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)
                                }

                            }, { error ->
                                error.printStackTrace()
                                isShopActivityUpdating = false
                                progress_wheel.stopSpinning()
                                Timber.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", ERROR:" + error.localizedMessage)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)

                            })
            )
        } catch (e: Exception) {
            isShopActivityUpdating = false
            e.printStackTrace()
        }
    }

    private fun callShopVisitImageUploadApi(unSyncedList: List<ShopVisitImageModelEntity>, isAllSync: Boolean, list_: ArrayList<ShopActivityEntity>?) {

        try {

            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedList[0].shop_id
            visitImageShop.visit_datetime = unSyncedList[0].visit_datetime

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            Timber.d("=======UPLOAD REVISIT SINGLE IMAGE INPUT PARAMS (AVERAGE SHOP)=======")
            Timber.d("USER ID======> " + visitImageShop.user_id)
            Timber.d("SESSION ID======> " + visitImageShop.session_token)
            Timber.d("SHOP ID=========> " + visitImageShop.shop_id)
            Timber.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
            Timber.d("IMAGE========> " + unSyncedList[0].shop_image)
            Timber.d("======================================================================")

            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithImage(visitImageShop, unSyncedList[0].shop_image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                progress_wheel.stopSpinning()
                                Timber.d("UPLOAD REVISIT SINGLE IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, unSyncedList[0].shop_id!!)

                                    val unSyncedAudioList = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                            ShopActivityEntityList[mPosition].shopid!!, ShopActivityEntityList[mPosition].visited_date!!)

                                    if (unSyncedAudioList != null && unSyncedAudioList.isNotEmpty()) {
                                        callShopVisitAudioUploadApi(unSyncedAudioList, false, null)
                                    }


                                } else {
                                    if (!isAllSync) {
                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                        Collections.reverse(ShopActivityEntityList)
                                        (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                    }
                                }

                            }, { error ->
                                Timber.d("UPLOAD REVISIT SINGLE IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                if (!isAllSync) {
                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)
                                    (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                } else {
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                }
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callShopVisitAudioUploadApi(unSyncedAudioList: List<ShopVisitAudioEntity>, isAllSync: Boolean, list_: ArrayList<ShopActivityEntity>?) {
        try {

            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedAudioList[0].shop_id
            visitImageShop.visit_datetime = unSyncedAudioList[0].visit_datetime

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            Timber.d("=======UPLOAD REVISIT SINGLE AUDIO INPUT PARAMS (AVERAGE SHOP)=======")
            Timber.d("USER ID======> " + visitImageShop.user_id)
            Timber.d("SESSION ID======> " + visitImageShop.session_token)
            Timber.d("SHOP ID=========> " + visitImageShop.shop_id)
            Timber.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
            Timber.d("AUDIO========> " + unSyncedAudioList[0].audio)
            Timber.d("======================================================================")

            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithAudio(visitImageShop, unSyncedAudioList[0].audio!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                progress_wheel.stopSpinning()
                                Timber.d("UPLOAD REVISIT SINGLE IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedAudioList[0].shop_id!!)

                                    if (!isAllSync) {

                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                        Collections.reverse(ShopActivityEntityList)

                                        averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                        (mContext as DashboardActivity).showSnackMessage("Sync successful")
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                    }
                                    /*j++
                                if (j < unSyncedList.size)
                                    callShopVisitImageUploadApi(unSyncedList)
                                else {
                                    j = 0
                                    //callShopDurationApi()
                                }*/
                                } else {
                                    if (!isAllSync) {
                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                        Collections.reverse(ShopActivityEntityList)
                                        (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                    }
                                }

                            }, { error ->
                                Timber.d("UPLOAD REVISIT SINGLE IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                if (!isAllSync) {
                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)
                                    (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                } else {
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                }
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initShopList() {
        //ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

        Collections.reverse(ShopActivityEntityList)

        if (ShopActivityEntityList.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE

            Timber.d("===========INIT ADAPTER FOR CURRENT DATE (AVERAGE SHOP)========")
            Timber.d("shop list size====> " + ShopActivityEntityList.size)
            Timber.d("current date====> " + AppUtils.getCurrentDateForShopActi())

            initAdapter()
        } else {
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }
    }


    @SuppressLint("WrongConstant")
    private fun initAdapter() {

        ShopActivityEntityList = ShopActivityEntityList.distinctBy { it.shopid }

        tv_frag_avg_shop_total_visit_count.text="Total Visit (Count) : ${ShopActivityEntityList.size}"
        averageShopListAdapter = AverageShopListAdapter(mContext, ShopActivityEntityList, object : AverageShopListClickListener {
            override fun onSyncClick(position: Int) {

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[position].shopid)

                if (shop != null) {

                    if (shop.isUploaded) {
                        if(!isShopActivityUpdating){
                            isShopActivityUpdating = true
                            if(AppUtils.isOnline(mContext)){
                                Handler().postDelayed(Runnable {
                                    checkToSyncShop(position)
                                }, 500)
                            }else{
                                isShopActivityUpdating = false
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                            }
                        }else{
                            println("print_tag_sync checkToSyncShop not call")
                        }
                    } else {
                        syncShop(position, shop)
                    }
                }
            }

            override fun OnItemClick(position: Int) {
                try {
                    (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, ShopActivityEntityList[position].shopid!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun OnMenuClick(position: Int, view: View) {
                initiatePopupWindow(view, position)
            }

        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        shopList.layoutManager = layoutManager
        shopList.adapter = averageShopListAdapter
    }

    private fun syncShop(position: Int, shop: AddShopDBModelEntity) {

        try {

            val addShopData = AddShopRequestData()
            //if (!shop.isUploaded) {
            addShopData.session_token = Pref.session_token
            addShopData.address = shop.address
            addShopData.owner_contact_no = shop.ownerContactNumber
            addShopData.owner_email = shop.ownerEmailId
            addShopData.owner_name = shop.ownerName
            addShopData.pin_code = shop.pinCode
            addShopData.shop_lat = shop.shopLat.toString()
            addShopData.shop_long = shop.shopLong.toString()
            addShopData.shop_name = shop.shopName.toString()
            addShopData.type = shop.type.toString()
            addShopData.shop_id = shop.shop_id
            addShopData.user_id = Pref.user_id

            if (!TextUtils.isEmpty(shop.dateOfBirth))
                addShopData.dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.dateOfBirth)

            if (!TextUtils.isEmpty(shop.dateOfAniversary))
                addShopData.date_aniversary = AppUtils.changeAttendanceDateFormatToCurrent(shop.dateOfAniversary)

            addShopData.assigned_to_dd_id = shop.assigned_to_dd_id
            addShopData.assigned_to_pp_id = shop.assigned_to_pp_id
            addShopData.added_date = shop.added_date
            addShopData.amount = shop.amount
            addShopData.area_id = shop.area_id
            addShopData.model_id = shop.model_id
            addShopData.primary_app_id = shop.primary_app_id
            addShopData.secondary_app_id = shop.secondary_app_id
            addShopData.lead_id = shop.lead_id
            addShopData.stage_id = shop.stage_id
            addShopData.funnel_stage_id = shop.funnel_stage_id
            addShopData.booking_amount = shop.booking_amount
            addShopData.type_id = shop.type_id

            addShopData.director_name = shop.director_name
            addShopData.key_person_name = shop.person_name
            addShopData.phone_no = shop.person_no

            if (!TextUtils.isEmpty(shop.family_member_dob))
                addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.family_member_dob)

            if (!TextUtils.isEmpty(shop.add_dob))
                addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.add_dob)

            if (!TextUtils.isEmpty(shop.add_doa))
                addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(shop.add_doa)

            addShopData.specialization = shop.specialization
            addShopData.category = shop.category
            addShopData.doc_address = shop.doc_address
            addShopData.doc_pincode = shop.doc_pincode
            addShopData.is_chamber_same_headquarter = shop.chamber_status.toString()
            addShopData.is_chamber_same_headquarter_remarks = shop.remarks
            addShopData.chemist_name = shop.chemist_name
            addShopData.chemist_address = shop.chemist_address
            addShopData.chemist_pincode = shop.chemist_pincode
            addShopData.assistant_contact_no = shop.assistant_no
            addShopData.average_patient_per_day = shop.patient_count
            addShopData.assistant_name = shop.assistant_name

            if (!TextUtils.isEmpty(shop.doc_family_dob))
                addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.doc_family_dob)

            if (!TextUtils.isEmpty(shop.assistant_dob))
                addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.assistant_dob)

            if (!TextUtils.isEmpty(shop.assistant_doa))
                addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(shop.assistant_doa)

            if (!TextUtils.isEmpty(shop.assistant_family_dob))
                addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.assistant_family_dob)

            addShopData.entity_id = shop.entity_id
            addShopData.party_status_id = shop.party_status_id
            addShopData.retailer_id = shop.retailer_id
            addShopData.dealer_id = shop.dealer_id
            addShopData.beat_id = shop.beat_id
            addShopData.assigned_to_shop_id = shop.assigned_to_shop_id
            addShopData.actual_address = shop.actual_address

            /////////////////
            var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(shop.shop_id,false)
            addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!
//////////////////////

            // duplicate shop api call
            addShopData.isShopDuplicate=shop.isShopDuplicate

            callAddShopApi(addShopData, shop.shopImageLocalPath, shop.doc_degree, position)
            //}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, degree_imgPath: String?, position: Int) {

        try {

            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }


            progress_wheel.spin()

            Timber.d("==========SyncShop Input Params (Average Shop)============")
            Timber.d("shop id=======> " + addShop.shop_id)
            val index = addShop.shop_id!!.indexOf("_")
            Timber.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
            Timber.d("shop added date=======> " + addShop.added_date)
            Timber.d("shop address=======> " + addShop.address)
            Timber.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
            Timber.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
            Timber.d("date aniversery=======> " + addShop.date_aniversary)
            Timber.d("dob=======> " + addShop.dob)
            Timber.d("shop owner phn no=======> " + addShop.owner_contact_no)
            Timber.d("shop owner email=======> " + addShop.owner_email)
            Timber.d("shop owner name=======> " + addShop.owner_name)
            Timber.d("shop pincode=======> " + addShop.pin_code)
            Timber.d("session token=======> " + addShop.session_token)
            Timber.d("shop lat=======> " + addShop.shop_lat)
            Timber.d("shop long=======> " + addShop.shop_long)
            Timber.d("shop name=======> " + addShop.shop_name)
            Timber.d("shop type=======> " + addShop.type)
            Timber.d("user id=======> " + addShop.user_id)
            Timber.d("amount=======> " + addShop.amount)
            Timber.d("area id=======> " + addShop.area_id)
            Timber.d("model id=======> " + addShop.model_id)
            Timber.d("primary app id=======> " + addShop.primary_app_id)
            Timber.d("secondary app id=======> " + addShop.secondary_app_id)
            Timber.d("lead id=======> " + addShop.lead_id)
            Timber.d("stage id=======> " + addShop.stage_id)
            Timber.d("funnel stage id=======> " + addShop.funnel_stage_id)
            Timber.d("booking amount=======> " + addShop.booking_amount)
            Timber.d("type id=======> " + addShop.type_id)

            if (shop_imgPath != null)
                Timber.d("shop image path=======> $shop_imgPath")

            Timber.d("director name=======> " + addShop.director_name)
            Timber.d("family member dob=======> " + addShop.family_member_dob)
            Timber.d("key person's name=======> " + addShop.key_person_name)
            Timber.d("phone no=======> " + addShop.phone_no)
            Timber.d("additional dob=======> " + addShop.addtional_dob)
            Timber.d("additional doa=======> " + addShop.addtional_doa)
            Timber.d("doctor family member dob=======> " + addShop.doc_family_member_dob)
            Timber.d("specialization=======> " + addShop.specialization)
            Timber.d("average patient count per day=======> " + addShop.average_patient_per_day)
            Timber.d("category=======> " + addShop.category)
            Timber.d("doctor address=======> " + addShop.doc_address)
            Timber.d("doctor pincode=======> " + addShop.doc_pincode)
            Timber.d("chambers or hospital under same headquarter=======> " + addShop.is_chamber_same_headquarter)
            Timber.d("chamber related remarks=======> " + addShop.is_chamber_same_headquarter_remarks)
            Timber.d("chemist name=======> " + addShop.chemist_name)
            Timber.d("chemist name=======> " + addShop.chemist_address)
            Timber.d("chemist pincode=======> " + addShop.chemist_pincode)
            Timber.d("assistant name=======> " + addShop.assistant_name)
            Timber.d("assistant contact no=======> " + addShop.assistant_contact_no)
            Timber.d("assistant dob=======> " + addShop.assistant_dob)
            Timber.d("assistant date of anniversary=======> " + addShop.assistant_doa)
            Timber.d("assistant family dob=======> " + addShop.assistant_family_dob)
            Timber.d("entity id=======> " + addShop.entity_id)
            Timber.d("party status id=======> " + addShop.party_status_id)
            Timber.d("retailer id=======> " + addShop.retailer_id)
            Timber.d("dealer id=======> " + addShop.dealer_id)
            Timber.d("beat id=======> " + addShop.beat_id)
            Timber.d("assigned to shop id=======> " + addShop.assigned_to_shop_id)
            Timber.d("actual address=======> " + addShop.actual_address)

            if (degree_imgPath != null)
                Timber.d("doctor degree image path=======> $degree_imgPath")
            Timber.d("====================================================")



            if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(degree_imgPath)) {
                val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addShop(addShop)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val addShopResult = result as AddShopResponse
                                    Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                    when (addShopResult.status) {
                                        NetworkConstant.SUCCESS -> {
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                            doAsync {
                                                val resultAs = runLongTask(addShop.shop_id)
                                                uiThread {
                                                    if (resultAs == true) {
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }
                                                }
                                            }

                                        }
                                        NetworkConstant.DUPLICATE_SHOP_ID -> {
                                            Timber.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                            //progress_wheel.stopSpinning()
                                            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                                AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)


                                                Timber.d("=======Duplicate shop deleted from shop activity table (Average Shop)============")
                                                AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                            }
                                            doAsync {
                                                val resultAs = runLongTask(addShop.shop_id)
                                                uiThread {
                                                    if (resultAs == true) {
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }

                                                }
                                            }
                                            //getAssignedPPListApi(addShop.shop_id, position)
                                        }
                                        else -> {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                        }
                                    }


                                }, { error ->
                                    error.printStackTrace()
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                    if (error != null)
                                        Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
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
                                                val resultAs = runLongTask(addShop.shop_id)
                                                uiThread {
                                                    if (resultAs == true) {
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }
                                                }
                                            }

                                        }
                                        NetworkConstant.DUPLICATE_SHOP_ID -> {
                                            Timber.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                            //progress_wheel.stopSpinning()
                                            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                                AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)


                                                Timber.d("=======Duplicate shop deleted from shop activity table (Average Shop)============")
                                                AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                            }
                                            doAsync {
                                                val resultAs = runLongTask(addShop.shop_id)
                                                uiThread {
                                                    if (resultAs == true) {
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }

                                                }
                                            }
                                            //getAssignedPPListApi(addShop.shop_id, position)
                                        }
                                        else -> {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                        }
                                    }


                                }, { error ->
                                    error.printStackTrace()
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                    if (error != null)
                                        Timber.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                                })
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runLongTask(shop_id: String?): Any {
        if (shop_id != null) {
            val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id, true, false)
            if (shopActivity != null)
                callShopActivitySubmit(shop_id)
            return true
        } else
            return false
    }

    private var shop_duration = ""
    private var startTimeStamp = ""
    private fun callShopActivitySubmit(shopId: String) {

        try {

            var list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
            if (list.isEmpty())
                return
            var shopDataList: MutableList<ShopDurationRequestData> = java.util.ArrayList()
            var shopDurationApiReq = ShopDurationRequest()
            shopDurationApiReq.user_id = Pref.user_id
            shopDurationApiReq.session_token = Pref.session_token

            if (!Pref.isMultipleVisitEnable) {
                var shopActivity = list[0]

                var shopDurationData = ShopDurationRequestData()
                shopDurationData.shop_id = shopActivity.shopid
                if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                    val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())

                    shopDurationData.spent_duration = duration
                } else {
                    shopDurationData.spent_duration = shopActivity.duration_spent
                }
                shopDurationData.visited_date = shopActivity.visited_date
                shopDurationData.visited_time = shopActivity.visited_date
                if (TextUtils.isEmpty(shopActivity.distance_travelled))
                    shopActivity.distance_travelled = "0.0"
                shopDurationData.distance_travelled = shopActivity.distance_travelled
                var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
                if (sList != null && sList.isNotEmpty())
                    shopDurationData.total_visit_count = sList[0].totalVisitCount

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

                shopDataList.add(shopDurationData)
            }
            else {
                for (i in list.indices) {
                    var shopActivity = list[i]

                    var shopDurationData = ShopDurationRequestData()
                    shopDurationData.shop_id = shopActivity.shopid
                    if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                        val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                        val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)

                        shopDurationData.spent_duration = duration
                    } else {
                        shopDurationData.spent_duration = shopActivity.duration_spent
                    }
                    shopDurationData.visited_date = shopActivity.visited_date
                    shopDurationData.visited_time = shopActivity.visited_date

                    if (TextUtils.isEmpty(shopActivity.distance_travelled))
                        shopActivity.distance_travelled = "0.0"

                    shopDurationData.distance_travelled = shopActivity.distance_travelled

                    var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
                    if (sList != null && sList.isNotEmpty())
                        shopDurationData.total_visit_count = sList[0].totalVisitCount

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

                    shopDataList.add(shopDurationData)
                }
            }

            if (shopDataList.isEmpty()) {
                return
            }

            shopDurationApiReq.shop_list = shopDataList
            val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

            BaseActivity.compositeDisposable.add(
                    repository.shopDuration(shopDurationApiReq)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                Timber.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + ", RESPONSE:" + result.message)
                                if (result.status == NetworkConstant.SUCCESS) {

                                }

                            }, { error ->
                                error.printStackTrace()
                                if (error != null)
                                    Timber.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + error.localizedMessage)
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAssignedPPListApi(shop_id: String?, position: Int) {

        try {
            println("bypass_tag call getAssignedPPListApi check AverageShopFragment")
            val repository = AssignToPPListRepoProvider.provideAssignPPListRepository()
            progress_wheel.spin()
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

                                            val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
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
                                                getAssignedDDListApi(shop_id, position)
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getAssignedDDListApi(shop_id, position)
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedDDListApi(shop_id, position)
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                getAssignedDDListApi(shop_id, position)
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAssignedDDListApi(shop_id: String?, position: Int) {

        try {
            val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
            progress_wheel.spin()
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

                                            val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
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
                                                getAssignedToShopApi(shop_id, position)
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getAssignedToShopApi(shop_id, position)
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi(shop_id, position)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                getAssignedToShopApi(shop_id, position)
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAssignedToShopApi(shop_id: String?, position: Int) {
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
                                        (mContext as DashboardActivity).showSnackMessage("Sync successful")

                                        if (!Pref.isMultipleVisitEnable)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate)
                                        else
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate, startTimeStamp)

                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                        Collections.reverse(ShopActivityEntityList)
                                        averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Sync successful")

                                if (!Pref.isMultipleVisitEnable)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate)
                                else
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate, startTimeStamp)

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                Collections.reverse(ShopActivityEntityList)
                                averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Sync successful")

                            if (!Pref.isMultipleVisitEnable)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate)
                            else
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate, startTimeStamp)

                            ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                            Collections.reverse(ShopActivityEntityList)
                            averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                        })
        )
    }

    private var mPosition = 0
    private fun checkToSyncShop(position: Int) {

        try {
            mPosition = position
            if (!ShopActivityEntityList[position].isUploaded)
                syncShopActivity(ShopActivityEntityList[position].shopid!!)
            else {
                isShopActivityUpdating = false
                val unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                        ShopActivityEntityList[position].shopid!!, ShopActivityEntityList[position].visited_date!!)

                if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                    callShopVisitImageUploadApi(unSyncedList, false, null)
                } else {
                    val unSyncedAudioList = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                            ShopActivityEntityList[position].shopid!!, ShopActivityEntityList[position].visited_date!!)

                    if (unSyncedAudioList != null && unSyncedAudioList.isNotEmpty()) {
                        callShopVisitAudioUploadApi(unSyncedAudioList, false, null)
                    }
                }
            }
        } catch (e: Exception) {
            isShopActivityUpdating = false
            e.printStackTrace()
        }
    }

    private fun initiatePopupWindow(view: View, position: Int) {
        val popup = PopupWindow(context)
        val layout = layoutInflater.inflate(R.layout.popup_window_shop_item, null)

        popup.contentView = layout
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        var call_ll: LinearLayout = layout.findViewById(R.id.call_ll)
        var direction_ll: LinearLayout = layout.findViewById(R.id.direction_ll)
        var add_order_ll: LinearLayout = layout.findViewById(R.id.add_order_ll)

        var call_iv: ImageView = layout.findViewById(R.id.call_iv)
        var call_tv: TextView = layout.findViewById(R.id.call_tv)
        var direction_iv: ImageView = layout.findViewById(R.id.direction_iv)
        var direction_tv: TextView = layout.findViewById(R.id.direction_tv)
        var order_iv: ImageView = layout.findViewById(R.id.order_iv)
        var order_tv: TextView = layout.findViewById(R.id.order_tv)


        call_ll.setOnClickListener(View.OnClickListener {
            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_select))

            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_deselect))
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_deselect))
            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
//            IntentActionable.initiatePhoneCall(mContext, list[position].)
        })

        direction_ll.setOnClickListener(View.OnClickListener {
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_select))

            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_deselect))
            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_deselect))
            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).openLocationWithTrack()

        })

        add_order_ll.setOnClickListener(View.OnClickListener {
            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_select))

            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_deselect))
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_deselect))
            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

        })

        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(view)
        popup.update()

    }

    private fun syncAllShopActivity(shopId: String, list_: ArrayList<ShopActivityEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        val mList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, selectedDate)
        if (mList.isEmpty())
            return
        val shopActivity = mList[0]
//        var shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)
        val shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token
        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val shopDurationData = ShopDurationRequestData()
        shopDurationData.shop_id = shopActivity.shopid
        if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
            val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
            val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

            if (!Pref.isMultipleVisitEnable) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
            }
            else {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
            }

            shopDurationData.spent_duration = duration
        } else {
            shopDurationData.spent_duration = shopActivity.duration_spent
        }
        shopDurationData.visited_date = shopActivity.visited_date
        shopDurationData.visited_time = shopActivity.visited_date
        if (TextUtils.isEmpty(shopActivity.distance_travelled))
            shopActivity.distance_travelled = "0.0"
        shopDurationData.distance_travelled = shopActivity.distance_travelled
        val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
        if (list != null && list.isNotEmpty())
            shopDurationData.total_visit_count = list[0].totalVisitCount

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
        shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey

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

        shopDataList.add(shopDurationData)

        if (shopDataList.isEmpty()) {
            return
        }

        Log.e("Average Shop", "isShopActivityUpdating====> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        Timber.d("========SYNC ALL VISITED SHOP DATA (AVERAGE SHOP)=====")
        Timber.d("SHOP ID======> " + shopDurationData.shop_id)
        Timber.d("SPENT DURATION======> " + shopDurationData.spent_duration)
        Timber.d("VISIT DATE=========> " + shopDurationData.visited_date)
        Timber.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
        Timber.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
        Timber.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
        Timber.d("FEEDBACK========> " + shopDurationData.feedback)
        Timber.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
        Timber.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
        Timber.d("next_visit_date========> " + shopDurationData.next_visit_date)
        Timber.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
        Timber.d("device_model========> " + shopDurationData.device_model)
        Timber.d("android_version========> " + shopDurationData.android_version)
        Timber.d("battery========> " + shopDurationData.battery)
        Timber.d("net_status========> " + shopDurationData.net_status)
        Timber.d("net_type========> " + shopDurationData.net_type)
        Timber.d("in_time========> " + shopDurationData.in_time)
        Timber.d("out_time========> " + shopDurationData.out_time)
        Timber.d("start_timestamp========> " + shopDurationData.start_timestamp)
        Timber.d("in_location========> " + shopDurationData.in_location)
        Timber.d("out_location========> " + shopDurationData.out_location)
        Timber.d("=======================================================")

        ////////
        revisitStatusList.clear()
        var key:String = ""
        try {
            for(i in 0..list_?.size-1){
                if(list_.get(i).shopid.equals(shopId)){
                    key=list_.get(i).shop_revisit_uniqKey!!.toString()
                }
            }
        }catch (ex:Exception){

        }
        try {
            var revisitStatusObj= ShopRevisitStatusRequestData()
            var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(key)
            if(data!=null ){
                revisitStatusObj.shop_id=data.shop_id
                revisitStatusObj.order_status=data.order_status
                revisitStatusObj.order_remarks=data.order_remarks
                revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                revisitStatusList.add(revisitStatusObj)
            }
        }catch (ex:java.lang.Exception){

        }

        ///////////

        progress_wheel.spin()
        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            Timber.d("ShopActivityFromAverageShop : RESPONSE STATUS:= " + result.status + ", RESPONSE MESSAGE:= " + result.message +
                                    "\nUser Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid +
                                    ", SHOP: " + mList[0].shop_name)
                            if (result.status == NetworkConstant.SUCCESS) {


                                if(!revisitStatusList.isEmpty()){
                                    callRevisitStatusUploadApi(revisitStatusList!!)
                                }

                                callCompetetorImgUploadApi(shopId)


                                if (!Pref.isMultipleVisitEnable)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate)
                                else
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate, shopActivity.startTimeStamp)

                                //
                                i++
                                if (i < list_.size) {

                                    /*val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
                                    for (i in shopDataList.indices) {
                                        val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

                                        if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                            unSyncedList.add(unSyncedData[0])
                                        }
                                    }*/
                                    progress_wheel.stopSpinning()
                                    /*if (unSyncedList.size > 0) {
                                        callShopVisitImageUploadApi(unSyncedList, true, list_)
                                    } else {*/
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_[i].shopid!!, list_)
                                    //}

                                }
                                else {
                                    i = 0
                                    val unSyncedList = ArrayList<ShopVisitImageModelEntity>()

                                    if (!Pref.isMultipleVisitEnable) {
                                        for (i in list_.indices) {
                                            //val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, list_[i].shopid!!, list_[i].visited_date!!)

                                            var unSyncedData: List<ShopVisitImageModelEntity>? = null

                                            if (AppUtils.isVisitSync == "1") {
                                                unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysListAccordingToShopId(
                                                        list_[i].shopid!!, list_[i].visited_date!!)
                                            } else {
                                                unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(
                                                        false, list_[i].shopid!!, list_[i].visited_date!!)
                                            }

                                            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                unSyncedList.add(unSyncedData[0])
                                            }
                                        }
                                    }

                                    progress_wheel.stopSpinning()
                                    if (unSyncedList.size > 0) {
                                        j = 0
                                        //callShopVisitImageUploadApi(unSyncedList, true, list_)
                                        BaseActivity.isShopActivityUpdating = false
                                        callShopVisitImageUploadApiForAll(unSyncedList)
                                    } else {

                                        val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in ShopActivityEntityList.indices) {
                                                if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                                                    var unSyncedData: List<ShopVisitAudioEntity>? = null

                                                    unSyncedData = if (AppUtils.isVisitSync == "1")
                                                        AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                                                    else
                                                        AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                                                ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                                                    if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                        unSyncedAudioList.add(unSyncedData[0])
                                                    }
                                                }
                                            }
                                        }

                                        if (unSyncedAudioList.isNotEmpty()) {
                                            j = 0
                                            BaseActivity.isShopActivityUpdating = false
                                            callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                                        } else {
                                            BaseActivity.isShopActivityUpdating = false

                                            val dateWiseList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                            Timber.d("=======UPDATE ADAPTER FOR SYNC ALL VISIT SHOP DATA (AVERAGE SHOP)=======")
                                            Timber.d("shop list size====> " + dateWiseList.size)
                                            Timber.d("specific date====> $selectedDate")

                                            averageShopListAdapter.updateList(dateWiseList)
                                            ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                            Collections.reverse(ShopActivityEntityList)
                                        }
                                    }

                                    /*BaseActivity.isShopActivityUpdating = false
                                    averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))

                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)*/
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                BaseActivity.isShopActivityUpdating = false
                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            if (error != null) {
                                Timber.d("ShopActivityFromAverageShop : ERROR:= " + error.localizedMessage + "\nUser Id" + Pref.user_id +
                                        ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }
                        })
        )

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


    private fun callCompetetorImgUploadApi(shop_id:String){
        //val unsynList = AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().getUnSyncedCopetetorImg(Pref.user_id!!)
        val unsynList = AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().getUnSyncedCopetetorImgByShopID(shop_id)
        var objCompetetor : AddShopRequestCompetetorImg = AddShopRequestCompetetorImg()

        if(unsynList == null || unsynList.size==0)
            return

        var shop_id:String

        //for(i in unsynList.indices){
        objCompetetor.session_token=Pref.session_token
        objCompetetor.shop_id=unsynList.get(0).shop_id
        objCompetetor.user_id=Pref.user_id
        objCompetetor.visited_date=unsynList.get(0).visited_date!!
        shop_id= unsynList.get(0).shop_id.toString()
        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        BaseActivity.compositeDisposable.add(
                repository.addShopWithImageCompetetorImg(objCompetetor,unsynList.get(0).shop_image,mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            if(response.status==NetworkConstant.SUCCESS){
                                AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().updateisUploaded(true,shop_id)
                                Timber.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shop_id + ", Success: ")
                            }else{
                                Timber.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shop_id + ", Failed: ")
                            }
                        },{
                            error ->
                            if (error != null) {
                                Timber.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shop_id + ", ERROR: " + error.localizedMessage)
                            }
                        })
        )

    }




    private fun callShopDurationApiNew() {
        if (Pref.user_id.isNullOrEmpty() || isShopActivityUpdating){
            simpleDialogProcess.dismiss()
            return
        }

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
                    //callShopDurationApiNewAfter()
                    Handler().postDelayed(Runnable {
                        progress_wheel.stopSpinning()
                        initShopList()
                    }, 500)
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
                                simpleDialogProcess.dismiss()
                                Timber.e("averageshopfrag callShopDurationApiNew response " + newShopList.size)
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
                                //ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                //callShopDurationApiNewAfter()
                                Handler().postDelayed(Runnable {
                                    progress_wheel.stopSpinning()
                                    initShopList()
                                }, 500)
                            }, { error ->
                                simpleDialogProcess.dismiss()
                                BaseActivity.isShopActivityUpdating = false
                                progress_wheel.stopSpinning()
                                if (error == null) {
                                    Timber.d("averageshopfrag callShopDurationApii : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                } else {
                                    Timber.d("averageshopfrag callShopDurationApii : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                                //ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                //callShopDurationApiNewAfter()
                                Handler().postDelayed(Runnable {
                                    progress_wheel.stopSpinning()
                                    initShopList()
                                }, 500)
                            })
                    )
                }

            }
        }
    }

    private fun callShopDurationApiNewAfter(){
        Handler().postDelayed(Runnable {
            if (!Pref.isMultipleVisitEnable) {
                if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty()) {

                    var list = ArrayList<ShopActivityEntity>()

                    for (i in ShopActivityEntityList.indices) {
                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                        if (shop.isUploaded) {
                            if (ShopActivityEntityList[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                                if (AppUtils.isVisitSync == "1")
                                    list.add(ShopActivityEntityList[i])
                                else {
                                    if (!ShopActivityEntityList[i].isUploaded)
                                        list.add(ShopActivityEntityList[i])
                                }
                            }
                        }
                    }

                    /*if(list.size>0){
                        for( i in list?.indices){
                            var revisitStatusObj= ShopRevisitStatusRequestData()
                            var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(list?.get(i).shop_revisit_uniqKey.toString())
                            if(data != null ){
                                revisitStatusObj.shop_id=data.shop_id
                                revisitStatusObj.order_status=data.order_status
                                revisitStatusObj.order_remarks=data.order_remarks
                                revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                                revisitStatusList.add(revisitStatusObj)
                            }
                        }
                    }*/


                    list = list.filter { it.isUploaded == false } as ArrayList<ShopActivityEntity>

                    if (list.size > 0)
                        syncAllShopActivity(list[i].shopid!!, list)
                    else
                        syncShopVisitImage()

                } else {
                    syncShopVisitImage()
                }
            }
            else {
                if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty()) {

                    var list = ArrayList<ShopActivityEntity>()

                    for (i in ShopActivityEntityList.indices) {
                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                        if (shop.isUploaded) {
                            if (ShopActivityEntityList[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                                if (AppUtils.isVisitSync == "1")
                                    list.add(ShopActivityEntityList[i])
                                else {
                                    if (!ShopActivityEntityList[i].isUploaded)
                                        list.add(ShopActivityEntityList[i])
                                }
                            }
                        }
                    }

                    list = list.filter { it.isUploaded == false } as ArrayList<ShopActivityEntity>

                    if (list.size > 0)
                        syncAllShopActivityForMultiVisit(list)
                }
            }
        }, 1700)
    }


}