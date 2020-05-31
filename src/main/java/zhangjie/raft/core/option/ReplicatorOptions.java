/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zhangjie.raft.core.option;


import zhangjie.raft.core.NodeImpl;
import zhangjie.raft.core.ReplicatorType;
import zhangjie.raft.core.entity.PeerId;
import zhangjie.raft.core.rpc.RaftClientService;
import zhangjie.raft.core.util.Copiable;

/**
 * Replicator options.
 *
 * @author boyan (boyan@alibaba-inc.com)
 *
 * 2018-Apr-04 2:59:24 PM
 */
public class ReplicatorOptions implements Copiable<ReplicatorOptions> {

    private int               dynamicHeartBeatTimeoutMs;
    private int               electionTimeoutMs;
    private String            groupId;
    private PeerId            serverId;
    private PeerId            peerId;
    private NodeImpl          node;
    private long              term;
    private RaftClientService raftRpcService;
    private ReplicatorType    replicatorType;

    public ReplicatorOptions() {
        super();
    }

    public ReplicatorOptions(final ReplicatorType replicatorType, final int dynamicHeartBeatTimeoutMs,
                             final int electionTimeoutMs, final String groupId, final PeerId serverId,
                             final PeerId peerId, final NodeImpl node, final long term,
                             final RaftClientService raftRpcService) {
        super();
        this.replicatorType = replicatorType;
        this.dynamicHeartBeatTimeoutMs = dynamicHeartBeatTimeoutMs;
        this.electionTimeoutMs = electionTimeoutMs;
        this.groupId = groupId;
        this.serverId = serverId;
        if (peerId != null) {
            this.peerId = peerId.copy();
        } else {
            this.peerId = null;
        }
        this.node = node;
        this.term = term;
        this.raftRpcService = raftRpcService;
    }

    public final ReplicatorType getReplicatorType() {
        return this.replicatorType;
    }

    public void setReplicatorType(final ReplicatorType replicatorType) {
        this.replicatorType = replicatorType;
    }

    public RaftClientService getRaftRpcService() {
        return this.raftRpcService;
    }

    public void setRaftRpcService(final RaftClientService raftRpcService) {
        this.raftRpcService = raftRpcService;
    }

    @Override
    public ReplicatorOptions copy() {
        final ReplicatorOptions replicatorOptions = new ReplicatorOptions();
        replicatorOptions.setDynamicHeartBeatTimeoutMs(this.dynamicHeartBeatTimeoutMs);
        replicatorOptions.setReplicatorType(this.replicatorType);
        replicatorOptions.setElectionTimeoutMs(this.electionTimeoutMs);
        replicatorOptions.setGroupId(this.groupId);
        replicatorOptions.setServerId(this.serverId);
        replicatorOptions.setPeerId(this.peerId);
        replicatorOptions.setNode(this.node);
        replicatorOptions.setTerm(this.term);
        replicatorOptions.setRaftRpcService(this.raftRpcService);
        return replicatorOptions;
    }

    public PeerId getPeerId() {
        return this.peerId;
    }

    public void setPeerId(final PeerId peerId) {
        if (peerId != null) {
            this.peerId = peerId.copy();
        } else {
            this.peerId = null;
        }
    }

    public int getDynamicHeartBeatTimeoutMs() {
        return this.dynamicHeartBeatTimeoutMs;
    }

    public void setDynamicHeartBeatTimeoutMs(final int dynamicHeartBeatTimeoutMs) {
        this.dynamicHeartBeatTimeoutMs = dynamicHeartBeatTimeoutMs;
    }

    public int getElectionTimeoutMs() {
        return this.electionTimeoutMs;
    }

    public void setElectionTimeoutMs(final int electionTimeoutMs) {
        this.electionTimeoutMs = electionTimeoutMs;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public PeerId getServerId() {
        return this.serverId;
    }

    public void setServerId(final PeerId serverId) {
        this.serverId = serverId;
    }


    public NodeImpl getNode() {
        return this.node;
    }

    public void setNode(final NodeImpl node) {
        this.node = node;
    }

    public long getTerm() {
        return this.term;
    }

    public void setTerm(final long term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "ReplicatorOptions{" +
                "dynamicHeartBeatTimeoutMs=" + dynamicHeartBeatTimeoutMs +
                ", electionTimeoutMs=" + electionTimeoutMs +
                ", groupId='" + groupId + '\'' +
                ", serverId=" + serverId +
                ", peerId=" + peerId +
                ", node=" + node +
                ", term=" + term +
                ", raftRpcService=" + raftRpcService +
                ", replicatorType=" + replicatorType +
                '}';
    }
}
