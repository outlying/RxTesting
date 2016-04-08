package com.antyzero.rxtesting.espresso;


import android.support.test.espresso.Espresso;

import com.antyzero.rxtesting.espresso.rx.CustomExecutorScheduler;
import com.antyzero.rxtesting.espresso.rx.SchedulersHook;
import com.antyzero.rxtesting.espresso.rx.ThreadPoolIdlingResource;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import rx.plugins.RxJavaSchedulersHook;
import rx.plugins.RxJavaTestPlugins;

/**
 * Bind custom ThreadPoolExecutor as custom IdlingResource to Espresso instance.
 */
public class RxSchedulersRule implements TestRule {

    private final RxJavaSchedulersHook schedulersHook;
    private final ThreadPoolIdlingResource idlingResource;

    /**
     *
     */
    public RxSchedulersRule() {
        ThreadPoolExecutor threadPoolExecutor = ( ThreadPoolExecutor ) Executors.newScheduledThreadPool( 16 );
        CustomExecutorScheduler scheduler = new CustomExecutorScheduler( threadPoolExecutor );
        this.schedulersHook = new SchedulersHook( scheduler );

        idlingResource = new ThreadPoolIdlingResource( threadPoolExecutor ) {
            @Override
            public String getName() {
                return getClass().getSimpleName();
            }
        };
    }

    @Override
    public Statement apply(Statement base, Description description ) {
        return new RxStatement( base, this );
    }

    /**
     * Prepare hooks and IdlingResource before and after test run
     */
    private static class RxStatement extends Statement {
        private final Statement base;
        private final RxSchedulersRule testRule;

        public RxStatement( Statement base, RxSchedulersRule schedulersHook ) {
            this.base = base;
            this.testRule = schedulersHook;
        }

        @Override
        public void evaluate() throws Throwable {

            RxJavaTestPlugins.resetPlugins();
            RxJavaTestPlugins.getInstance().registerSchedulersHook( testRule.schedulersHook );
            Espresso.registerIdlingResources( testRule.idlingResource );

            base.evaluate();

            Espresso.unregisterIdlingResources( testRule.idlingResource );
            RxJavaTestPlugins.resetPlugins();
        }
    }
}
