package com.myexecutor.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private LinearLayout containerView;
    private boolean isExpanded = false;

    static {
        System.loadLibrary("executor");
    }

    public native void nativeInjectLua(String luaScript);
    public native boolean nativeInitHooks();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        nativeInitHooks();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        containerView = new LinearLayout(this);
        containerView.setOrientation(LinearLayout.VERTICAL);
        containerView.setBackgroundColor(Color.parseColor("#CC1E1E1E"));
        containerView.setPadding(16, 16, 16, 16);

        final Button toggleBtn = new Button(this);
        toggleBtn.setText("EXEC");
        toggleBtn.setBackgroundColor(Color.parseColor("#FF3B82F6"));
        toggleBtn.setTextColor(Color.WHITE);

        final EditText scriptEditor = new EditText(this);
        scriptEditor.setHint("Write Lua Code Here");
        scriptEditor.setTextColor(Color.WHITE);
        scriptEditor.setHintTextColor(Color.GRAY);
        scriptEditor.setMinLines(5);
        scriptEditor.setVisibility(View.GONE);

        final Button injectBtn = new Button(this);
        injectBtn.setText("Inject Lua");
        injectBtn.setBackgroundColor(Color.parseColor("#FF22C55E"));
        injectBtn.setTextColor(Color.WHITE);
        injectBtn.setVisibility(View.GONE);

        containerView.addView(toggleBtn);
        containerView.addView(scriptEditor);
        containerView.addView(injectBtn);

        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 100;

        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    scriptEditor.setVisibility(View.VISIBLE);
                    injectBtn.setVisibility(View.VISIBLE);
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    windowManager.updateViewLayout(containerView, params);
                    isExpanded = true;
                } else {
                    scriptEditor.setVisibility(View.GONE);
                    injectBtn.setVisibility(View.GONE);
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    windowManager.updateViewLayout(containerView, params);
                    isExpanded = false;
                }
            }
        });

        injectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = scriptEditor.getText().toString();
                if (!code.isEmpty()) {
                    nativeInjectLua(code);
                    Toast.makeText(FloatingService.this, "Script Injected!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        windowManager.addView(containerView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (containerView != null) {
            windowManager.removeView(containerView);
        }
    }
}
