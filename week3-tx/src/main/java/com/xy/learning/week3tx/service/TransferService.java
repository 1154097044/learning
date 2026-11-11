package com.xy.learning.week3tx.service;

import com.xy.learning.week3tx.common.BizException;
import com.xy.learning.week3tx.entity.Account;
import com.xy.learning.week3tx.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository repo;

    /**
     * 自注入：注入的是"本 Bean 的代理对象"，不是 this。
     * 用 self.xxx() 调用就能走代理、让 @Transactional 生效，是修复自调用失效的手段之一。
     * @Lazy 是为了避免"自己依赖自己"在初始化时的循环依赖问题。
     */
    @Lazy
    @Autowired
    private TransferService self;

    public TransferService(AccountRepository repo){
        this.repo = repo;
    }

    /** 场景1：正常转账，扣款+加款都在同一事务里，成功 → 提交 */
    @Transactional
    public Map<String, BigDecimal> transferSuccess(String from,String to,BigDecimal amount){
        debit(from,amount);
        credit(to,amount);
        log.info(">>> 场景1 转账成功 {} -> {} {}", from, to, amount);
        return balances();
    }

    /**
     * 场景2：扣款后、加款前抛异常。
     * 经代理调用（Controller 直接调本方法）时 @Transactional 生效 → 扣款回滚 → 余额不变。
     */
    @Transactional
    public Map<String,BigDecimal> transferFailBeforeCredit(String from,String to,BigDecimal amount){
        debit(from,amount);
        // 故意在加款前抛 RuntimeException：默认只回滚 RuntimeException/Error，正好触发回滚
        throw new IllegalStateException("模拟：加款方账户出错，转账中断");
    }

    /**
     * 场景3（BUG 复现）：本方法【没有】@Transactional，内部用 this. 自调用场景2。
     * this. 调用绕过代理 → 场景2 的 @Transactional 失效 → debit 走 auto-commit 立即落库，
     * 随后抛异常也不会回滚 → from 的钱被扣了、to 没加 = 部分提交，钱凭空消失。
     */
    public Map<String,BigDecimal> selfInvocationBug(String from,String to,BigDecimal amount){
       try{
           this.transferFailBeforeCredit(from,to,amount);
       }catch (Exception e){
           log.warn(">>> 场景3 自调用 bug 捕获异常: {}", e.getMessage());
       }
        // 事务失效：debit 已 auto-commit 提交，这里查到的 from 余额是"被扣掉之后"的值 = bug
       return balances();
    }

    /**
     * 场景4（修复）：本方法同样【没有】@Transactional，但内部用 self. 调用场景2。
     * self 是注入的代理对象 → 经过代理 → 场景2 的 @Transactional 生效 → 抛异常回滚 → 余额不变。
     */
    public Map<String,BigDecimal> selfInvocationFixed(String from,String to,BigDecimal amount){
        try{
            self.transferFailBeforeCredit(from,to,amount);
        }catch (Exception e){
            log.warn(">>> 场景4 修复版捕获异常: {}", e.getMessage());
        }
        // 事务生效：debit 回滚，这里查到的 from 余额与转账前一致 = 修复成功
        return balances();
    }

    /** 重置余额，方便反复实验（每次 self-bug 都会真扣钱） */
    @Transactional
    public Map<String,BigDecimal> reset(){
        repo.findByName("Alice").ifPresent(a -> {
            a.setBalance(new BigDecimal("100.00"));
            repo.save(a);
        });
        repo.findByName("Bob").ifPresent(b -> {
            b.setBalance(new BigDecimal("50.00"));
            repo.save(b);
        });
        log.info(">>> 余额已重置 Alice=100, Bob=50");
        return balances();
    }

    /** 当前所有账户余额快照（只读，不开事务也行） */
    public Map<String,BigDecimal> balances(){
        Map<String,BigDecimal> snapshot = new LinkedHashMap<>();
        repo.findAll().forEach(a -> snapshot.put(a.getName(),a.getBalance()));
        return snapshot;
    }

    /**
     * 按名查单个账户余额。
     * 账户不存在时抛 BizException.notFound（RuntimeException 子类），
     * 由 GlobalExceptionHandler 映射成 HTTP 404 + 业务码 40400，而不是笼统的 500。
     */
    public Map<String,BigDecimal> findByName(String name){
        Map<String,BigDecimal> snapshot = new LinkedHashMap<>();
        Account a = requireAccount(name);
        snapshot.put(a.getName(),a.getBalance());
        return snapshot;
    }

    // ---- 私有辅助：扣款 / 加款 ----
    private void debit(String name,BigDecimal amount){
        Account a = requireAccount(name);
        a.setBalance(a.getBalance().subtract(amount));
        repo.save(a);
    }

    private void credit(String name,BigDecimal amount){
        Account a = requireAccount(name);
        a.setBalance(a.getBalance().add(amount));
        repo.save(a);
    }

    /**
     * 取账户，不存在就抛 404 语义的业务异常。
     *
     * <p>用 BizException 而不是 IllegalArgumentException 有两个原因：
     * 一是能被 GlobalExceptionHandler 精确映射成 HTTP 404；
     * 二是它继承 RuntimeException，在 @Transactional 方法里抛出时事务照常回滚
     * （Spring 默认只对 RuntimeException/Error 回滚），不会破坏场景2/4 的回滚演示。
     */
    private Account requireAccount(String name){
        return repo.findByName(name)
                .orElseThrow(() -> BizException.notFound("账户不存在: " + name));
    }
}
