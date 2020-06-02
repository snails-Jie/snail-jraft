package zhangjie.raft.core.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhangjie.raft.core.option.RpcOptions;
import zhangjie.raft.core.util.Endpoint;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author zhangjie
 * @Date 2020/6/2 7:27
 **/
public abstract class AbstractClientService implements ClientService {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractClientService.class);

    protected volatile RpcClient  rpcClient;
    protected ThreadPoolExecutor rpcExecutor;
    protected RpcOptions          rpcOptions;

    /**
     * 初始化属性rpcClient、rpcExecutor、rpcOptions
     * @param opts 初始化参数
     * @return 初始化成功返回true
     */
    @Override
    public boolean init(RpcOptions opts) {
        if (this.rpcClient != null) {
            return true;
        }
        this.rpcOptions = rpcOptions;
        return initRpcClient(this.rpcOptions.getRpcProcessorThreadPoolSize());
    }

    protected boolean initRpcClient(final int rpcProcessorThreadPoolSize) {
        return true;
    }

    @Override
    public boolean connect(Endpoint endpoint) {
        return false;
    }
}
