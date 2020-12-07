package com.github.raftsman.queue

import com.lmax.disruptor.EventFactory

class EventFactory : EventFactory<EventWrapper> {

    override fun newInstance(): EventWrapper {
        return EventWrapper()
    }

}