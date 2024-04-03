package com.breezedsm.features.attendance.api

import com.breezedsm.app.NetworkConstant
import com.breezedsm.features.attendance.model.AttendSummResponse
import com.breezedsm.features.attendance.model.AttendanceRequest
import com.breezedsm.features.attendance.model.AttendanceResponse
import com.breezedsm.features.attendance.model.DayStartEndListResponse
import com.breezedsm.features.login.model.GetConcurrentUserResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 28-11-2017.
 */
interface AttendanceListApi {
    /*@POST("Attendance/Records")*/ @POST("Attendance/List")
    fun getAttendanceList(@Body attendanceReq: AttendanceRequest?): Observable<AttendanceResponse>

    /*@POST("Attendance/Records")*/ @POST("UserWiseDayStartEnd/UserDayStartEndList")
    fun getDayStartEndListAPI(@Body attendanceReq: AttendanceRequest?): Observable<DayStartEndListResponse>

    @FormUrlEncoded
    @POST("UserWiseDayStartEnd/UserAttendanceSummary")
    fun getAttendanceSummApi(@Field("user_id") user_id: String): Observable<AttendSummResponse>

    /**
     * Companion object to create the AttendanceListApi
     */
    companion object Factory {
        fun create(): AttendanceListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AttendanceListApi::class.java)
        }
    }

}