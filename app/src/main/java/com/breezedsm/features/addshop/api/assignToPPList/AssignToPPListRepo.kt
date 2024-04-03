package com.breezedsm.features.addshop.api.assignToPPList

import com.breezedsm.app.Pref
import com.breezedsm.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import io.reactivex.Observable

/**
 * Created by Saikat on 03-10-2018.
 */
class AssignToPPListRepo(val apiService: AssignToPPListApi) {
    fun assignToPPList(state_id: String): Observable<AssignToPPListResponseModel> {
        println("bypass_tag call getAssignedPPListApi check AssignToPPListRepo")
        return apiService.getAssignedToPPList(Pref.session_token!!, Pref.user_id!!, state_id)
    }
}