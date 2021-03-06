package tti.lv.airdockapp.screens.main.requests.parts


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_request_details.*
import tti.lv.airdockapp.App

import tti.lv.airdockapp.R
import tti.lv.airdockapp.screens.main.requests.RequestViewModel
import tti.lv.airdockapp.web.dto.RequestDTO
import javax.inject.Inject

class RequestDetailsFragment : Fragment() {

    @Inject lateinit var mViewModel : RequestViewModel

    private val mDisp = mutableListOf<Disposable>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_request_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.application as App).dependencyGraph.inject(this)

        mDisp += mViewModel.requests().subscribe{ setActiveRequest() }

        mDisp += mViewModel.requestSelectedEvent().subscribe{ request -> setActiveRequest(request) }
        mDisp += mViewModel.requestUpdateEvent().subscribe{ request -> setActiveRequest(request) }
        mDisp += mViewModel.requestStatuses().subscribe{ status -> setActiveReqestStatus(status) }

        mDisp += btnEdit.clicks().subscribe{ launchEditRequestActivity() }

        mDisp += btnSave.clicks().subscribe{ setRequestStatus(RequestDTO.Status.PENDING) }
        mDisp += btnDelete.clicks().subscribe{ deleteRequest() }

    }

    public fun deleteRequest() {
        mViewModel.deleteRequest()
    }

    public fun setRequestStatus(status : RequestDTO.Status) {
        mViewModel.setRequestStatus(status)
    }

    private fun setActiveReqestStatus(status: RequestDTO.Status?) {

        if (status!= null) btnChangeStatus.setText(status.toPrettyString())

        if (status == RequestDTO.Status.DRAFT) {
            btnEdit.visibility   = VISIBLE
            btnDelete.visibility = VISIBLE
            btnSave.visibility   = VISIBLE
        }
        else if (status == RequestDTO.Status.PENDING) {
            btnEdit.visibility   = VISIBLE
            btnDelete.visibility = GONE
            btnSave.visibility   = GONE
        }
        else {
            btnEdit.visibility   = GONE
            btnDelete.visibility = GONE
            btnSave.visibility   = GONE
        }

    }

    private fun setActiveRequest(request : RequestDTO) {
        with(request) {
            textRequestId.text          = id
            textRequestTitle.text       = title
            textRequestDescription.text = description
        }
    }

    private fun setActiveRequest() {
        with(mViewModel.getActiveRequest()) {
            if (this != null) {
                textRequestId.text          = id
                textRequestTitle.text       = title
                textRequestDescription.text = description
            }
        }
    }

    fun launchEditRequestActivity() {
        if (activity != null) {
            activity!!.startActivity(Intent(context, RequestEditActivity::class.java))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mDisp.forEach{ it.dispose() }
    }

}
