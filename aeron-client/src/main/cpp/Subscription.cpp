/*
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "Subscription.h"
#include "ClientConductor.h"

namespace aeron {

Subscription::Subscription(
    ClientConductor &conductor,
    std::int64_t registrationId,
    const std::string &channel,
    std::int32_t streamId,
    std::int32_t channelStatusId) :
    m_conductor(conductor),
    m_channel(channel),
    m_channelStatusId(channelStatusId),
    m_registrationId(registrationId),
    m_streamId(streamId),
    m_imageArray(),
    m_isClosed(false)
{
}

Subscription::~Subscription()
{
    auto imageArrayPair = m_imageArray.load();

    m_conductor.releaseSubscription(m_registrationId, imageArrayPair.first, imageArrayPair.second);
}

void Subscription::addDestination(const std::string& endpointChannel)
{
    if (isClosed())
    {
        throw util::IllegalStateException(std::string("Subscription is closed"), SOURCEINFO);
    }

    m_conductor.addRcvDestination(m_registrationId, endpointChannel);
}

void Subscription::removeDestination(const std::string& endpointChannel)
{
    if (isClosed())
    {
        throw util::IllegalStateException(std::string("Subscription is closed"), SOURCEINFO);
    }

    m_conductor.removeRcvDestination(m_registrationId, endpointChannel);
}

std::int64_t Subscription::channelStatus() const
{
    if (isClosed())
    {
        return ChannelEndpointStatus::NO_ID_ALLOCATED;
    }

    return m_conductor.channelStatus(m_channelStatusId);
}

}
