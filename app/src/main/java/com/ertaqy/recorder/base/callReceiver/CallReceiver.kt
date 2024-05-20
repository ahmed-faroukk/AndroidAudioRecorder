package com.ertaqy.recorder.base.callReceiver

import android.content.Context
import com.aykuttasil.callrecord.CallRecord
import com.aykuttasil.callrecord.receiver.CallRecordReceiver
import java.util.Date

class CallReceiver(callRecord: CallRecord) : CallRecordReceiver(callRecord) {
    override fun onIncomingCallReceived(context: Context, number: String?, start: Date) {
        super.onIncomingCallReceived(context, number, start)
    }
}
