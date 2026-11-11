package com.xy.learning.week3tx.web;

import com.xy.learning.week3tx.common.Result;
import com.xy.learning.week3tx.dto.TransferRequest;
import com.xy.learning.week3tx.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 事务 Demo 入口。
 *
 * <p>参数接收方式按语义分成两类：
 * <ul>
 *   <li><b>只读查询</b>（/balances、/find）—— 幂等无副作用，/find 同时开 GET 和 POST，
 *       走 @RequestParam，因此 query string 和 form-urlencoded 表单体都能解析
 *       （Tomcat 的 parseBodyMethods 默认只放行 POST，GET 的 body 不会进 parameterMap）。</li>
 *   <li><b>有副作用的转账</b>（/ok、/rollback、/self-bug、/self-fixed）—— 只开 POST，
 *       走 @Valid @RequestBody + TransferRequest DTO。不开 GET 是刻意的：
 *       GET 会被浏览器/代理缓存，且不受 CSRF 防护约束，转账接口开 GET 等于同时开两个口子。</li>
 * </ul>
 *
 * <p>所有响应统一包在 {@link Result} 里，错误响应由 GlobalExceptionHandler 兜。
 */
@RestController
@RequestMapping("/api/tx")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /** 场景1：正常转账，提交 */
    @PostMapping("/ok")
    public Result<Map<String, Object>> ok(@Valid @RequestBody TransferRequest request) {
        return Result.success(result("正常事务:扣款+加款成功，已提交",
                transferService.transferSuccess(request.from(), request.to(), request.amount())));
    }

    /**
     * 场景2：经代理调用 + 抛异常 → 回滚。
     * 这里刻意自己 catch：Demo 要把"回滚后的余额"返回给调用方观察，
     * 如果直接往外抛，GlobalExceptionHandler 会接管，就拿不到 balances 了。
     */
    @PostMapping("/rollback")
    public Result<Map<String, Object>> rollback(@Valid @RequestBody TransferRequest request) {
        try {
            transferService.transferFailBeforeCredit(request.from(), request.to(), request.amount());
            return Result.success(result("不应走到这里", transferService.balances()));
        } catch (Exception e) {
            return Result.success(result("经代理调用，@Transactional 生效，已回滚：" + e.getMessage(),
                    transferService.balances()));
        }
    }

    /** 场景3：自调用 BUG → 部分提交 */
    @PostMapping("/self-bug")
    public Result<Map<String, Object>> selfBug(@Valid @RequestBody TransferRequest request) {
        return Result.success(result("自调用(this.)经过代理，@Transactional 失效-> 部分提交 BUG: from 被扣款但未回滚",
                transferService.selfInvocationBug(request.from(), request.to(), request.amount())));
    }

    /** 场景4：自注入代理修复 → 回滚 */
    @PostMapping("/self-fixed")
    public Result<Map<String, Object>> selfFixed(@Valid @RequestBody TransferRequest request) {
        return Result.success(result("自注入(self.)走代理，@Transactional 生效 -> 已回滚，余额不变",
                transferService.selfInvocationFixed(request.from(), request.to(), request.amount())));
    }

    /** 重置余额，方便反复实验 */
    @PostMapping("/reset")
    public Result<Map<String, Object>> reset() {
        return Result.success(result("余额已重置", transferService.reset()));
    }

    /** 查当前余额 */
    @GetMapping("/balances")
    public Result<Map<String, Object>> balances() {
        return Result.success(result("当前余额", transferService.balances()));
    }

    /**
     * 按账户名查余额：GET / POST 双支持。
     *
     * <p>路由层靠 @RequestMapping(method = {GET, POST}) 放开两种方法；
     * 参数层靠 @RequestParam 天然通吃——它读的是容器的 getParameterMap()，
     * query string 和 POST 的 form-urlencoded 表单体都在里面。
     * 声明了 GET 就自动支持 HEAD（RequestMethodsRequestCondition 里对 HEAD→GET 有特判）。
     *
     * <p>账户不存在时 Service 抛 BizException.notFound，由 GlobalExceptionHandler 映射成 HTTP 404。
     */
    @RequestMapping(value = "/find", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Map<String, Object>> findBalanceByName(@RequestParam String name) {
        return Result.success(result("查询用户余额", transferService.findByName(name)));
    }

    private Map<String, Object> result(String message, Map<String, BigDecimal> balances) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("balances", balances);
        return body;
    }
}
