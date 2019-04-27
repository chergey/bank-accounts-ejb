package org.elcer.accounts;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.openejb.testing.Application;
import org.apache.tomee.embedded.TomEEEmbeddedApplicationRunner;
import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.services.AccountRepository;
import org.elcer.accounts.services.AccountService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Application
@RunWith(RepeatableRunner.class)
public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
    private static BeanManager savedBeanManager;

    @Inject
    private AccountService accountService;

    @Inject
    private BeanManager beanManager;


    @Inject
    private AccountRepository accountRepository;


    @BeforeClass
    public static void setUp() {
        final TomEEEmbeddedApplicationRunner runner = new TomEEEmbeddedApplicationRunner();
        runner.start(new AppTest());


    }

    @PostConstruct
    public void init() {
        savedBeanManager = beanManager;
    }


    @Test
    public void testConcurrencyAndDeadlocks() {
        final int times = 1400;

        accountRepository = getBean(AccountRepository.class);

        var first = accountRepository.save(new Account("Mike", BigDecimal.valueOf(26000)));
        var second = accountRepository.save(new Account("Jenny", BigDecimal.valueOf((315000))));
        var third = accountRepository.save(new Account("David", BigDecimal.valueOf((313000))));
        var fourth = accountRepository.save(new Account("Steve", BigDecimal.valueOf(356000)));

        var startingTotal = first.getBalance()
                .add(second.getBalance())
                .add(third.getBalance())
                .add(fourth.getBalance());

        ExecutorUtils.runConcurrentlyFJP(
                () -> transfer(times, first, second),
                () -> transfer(times, second, first),
                () -> transfer(times, third, second),

                () -> transfer(times, second, fourth),
                () -> transfer(times, second, third),
                () -> transfer(times, first, third),
                () -> transfer(times, first, fourth),

                () -> transfer(times, third, first),
                () -> transfer(times, third, fourth),

                () -> transfer(times, fourth, first),
                () -> transfer(times, fourth, second),
                () -> transfer(times, fourth, third)

        );

        var firstInTheEnd = accountRepository.retrieveAccountById(first.getId());
        var secondInTheEnd = accountRepository.retrieveAccountById(second.getId());
        var thirdInTheEnd = accountRepository.retrieveAccountById(third.getId());
        var fourthInTheEnd = accountRepository.retrieveAccountById(fourth.getId());

        var endingTotal = firstInTheEnd.getBalance()
                .add(secondInTheEnd.getBalance())
                .add(thirdInTheEnd.getBalance())
                .add(fourthInTheEnd.getBalance())
                .setScale(0, RoundingMode.UNNECESSARY);

        Assert.assertTrue("Balance can't be less than zero", firstInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertTrue("Balance can't be less than zero", secondInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertTrue("Balance can't be less than zero", thirdInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertTrue("Balance can't be less than zero", fourthInTheEnd.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        Assert.assertEquals(startingTotal, endingTotal);

    }

    private void transfer(final int times, Account debit, Account credit) {

        accountService = getBean(AccountService.class);

        int i = times;
        while (i-- >= 0) {
            try {
                accountService.transfer(debit.getId(), credit.getId(),
                        BigDecimal.valueOf(RandomUtils.nextLong(10, 1000)));
            } catch (Exception e) {
                if (e instanceof NotEnoughFundsException) {
                    logger.info("Not enough money left in {}, stopping", debit.getId());
                    break;
                } else
                    throw e;
            }
        }
    }


    private static <T> T getBean(Class<T> clazz) {
        Set<Bean<?>> beans = savedBeanManager.getBeans(clazz);
        if (beans.size() != 1) {
            return null;
        }

        @SuppressWarnings("unchecked") Bean<T> bean = (Bean<T>) CollectionUtils.extractSingleton(beans);
        CreationalContext<T> ctx = savedBeanManager.createCreationalContext(bean);
        @SuppressWarnings("unchecked") T object = (T) savedBeanManager.getReference(bean, clazz, ctx);

        Assert.assertNotNull("Can't be null", object);
        return object;

    }
}