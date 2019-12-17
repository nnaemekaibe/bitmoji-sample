package com.bibibryan.bitmoji_sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bibibryan.bitmoji_sample.Chat.ChatAdapter;
import com.bibibryan.bitmoji_sample.Chat.model.ChatImageMessage;
import com.bibibryan.bitmoji_sample.Chat.model.ChatImageUrlMessage;
import com.bibibryan.bitmoji_sample.Chat.model.ChatMessage;
import com.bibibryan.bitmoji_sample.Chat.model.ChatTextMessage;
import com.snapchat.kit.sdk.SnapKit;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.bitmoji.OnBitmojiSearchFocusChangeListener;
import com.snapchat.kit.sdk.bitmoji.OnBitmojiSelectedListener;
import com.snapchat.kit.sdk.bitmoji.ui.BitmojiFragment;
import com.snapchat.kit.sdk.bitmoji.ui.BitmojiFragmentSearchMode;
import com.snapchat.kit.sdk.bitmoji.ui.BitmojiIconFragment;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, LoginStateController.OnLoginStateChangedListener, ViewTreeObserver.OnGlobalLayoutListener,
        OnBitmojiSelectedListener,
        OnBitmojiSearchFocusChangeListener
{

    private static final String EXTERNAL_ID_QUERY = "{me{externalId}}";
    private static final float BITMOJI_CONTAINER_FOCUS_WEIGHT = 9.0f;
    private Button sendButton;

    private View mContentView;
    private View mBitmojiContainer;
    private View mFriendmojiToggle;
    private EditText mTextField;
    private RecyclerView mChatView;
    private BitmojiFragment mBitmojiFragment;

    private int mBitmojiContainerHeight;

    private final ChatAdapter mAdapter = new ChatAdapter();
    private boolean mIsBitmojiVisible = true;
    private boolean mShowingFriendmoji = false;
    private String mMyExternalId;

    private int mBaseRootViewHeightDiff = 0;
    private int mBitmojiisSent = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        setUpChatRecyclerView();
        setUpInputField();
        setUpFriendmoji();
        setUpViewListeners();
        mBitmojiContainerHeight = getResources().getDimensionPixelSize(R.dimen.bitmoji_container_height);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(this);
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mTextField.requestFocus();


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sdk_container, BitmojiFragment.builder()
                        .withShowSearchBar(true)
                        .withShowSearchPills(true)
                        .withTheme(R.style.MyBitmojiTheme)
                        .build())
                .commit();


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.bitmoji_button, new BitmojiIconFragment())
                .commit();


        if(SnapLogin.isUserLoggedIn(this)){
            loadExternalId();
        }
    }

    private void setUpViewListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText();
            }
        });

        findViewById(R.id.unlink_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapKit.unlink(MainActivity.this);
            }
        });

        findViewById(R.id.bitmoji_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentFocus() != mTextField) {
                    setBitmojiVisible(!mIsBitmojiVisible);
                }
                defocusInput();
            }
        });
    }

    private void setUpFriendmoji() {
        mFriendmojiToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sdk_container);
                if (fragment instanceof BitmojiFragment) {
                    ((BitmojiFragment) fragment).setFriend(mShowingFriendmoji ? null : mMyExternalId);
                }
                mShowingFriendmoji = !mShowingFriendmoji;
            }
        });
    }

    private void setUpInputField() {
        mTextField.setOnEditorActionListener(this);
        mTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    setBitmojiVisible(true);
                }
            }
        });
        mTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendButton.setEnabled(editable.length() > 0);
                if (mBitmojiFragment != null) {
                    mBitmojiFragment.setSearchText(editable.toString(), BitmojiFragmentSearchMode.SEARCH_RESULT_ONLY);
                }
            }
        });
    }

    private void setUpChatRecyclerView() {
        mAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mChatView.setLayoutManager(linearLayoutManager);
        mChatView.setAdapter(mAdapter);
    }

    private void setUpViews() {
        sendButton = findViewById(R.id.send_button);

        mContentView = findViewById(R.id.content_view);
        mBitmojiContainer = findViewById(R.id.sdk_container);
        mTextField = findViewById(R.id.input_field);
        mFriendmojiToggle = findViewById(R.id.friendmoji_toggle);
        mChatView = findViewById(R.id.chat);

    }








    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendText();
            textView.requestFocus();
            return true;
        }

        return false;
    }

    @Override
    public void onLoginSucceeded() {
        loadExternalId();
    }

    @Override
    public void onLoginFailed() {

    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onGlobalLayout() {
        int heightDiff = getRootViewHeightDiff(mContentView);

        if (heightDiff > getResources().getDimensionPixelSize(R.dimen.min_keyboard_height)) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBitmojiContainer.getLayoutParams();
            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            mBitmojiContainerHeight = heightDiff - mBaseRootViewHeightDiff;
            layoutParams.height = mBitmojiContainerHeight;
            mBitmojiContainer.setLayoutParams(layoutParams);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        } else {
            mBaseRootViewHeightDiff = heightDiff;
        }

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof BitmojiFragment) {
            // Set the reference to the sticker picker fragment
            // This should be done here in order to ensure that the reference is valid
            // even when the containing activity or fragment is recreated
            mBitmojiFragment = (BitmojiFragment) fragment;
        } else if (fragment instanceof BitmojiIconFragment) {
            // Attach the icon to the sticker picker to enable displaying a preview of
            // results from BitmojiFragment#setSearchText()
            // Note: This assumes that the icon is attached after the sticker picker
            //       Ensure that the order is correct when adding these
            if (mBitmojiFragment != null) {
                mBitmojiFragment.attachBitmojiIcon((BitmojiIconFragment) fragment);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect hitRect = new Rect();

        mChatView.getHitRect(hitRect);

        if(hitRect.contains((int) ev.getX(), (int) ev.getY())){
            //We clicked on the chat view.
            defocusInput();
            setBitmojiVisible(false);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBitmojiSearchFocusChange(boolean hasFocus) {
        getWindow().setSoftInputMode(hasFocus
                ? WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                : WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) mBitmojiContainer.getLayoutParams();

        // Set container height to 90% of available space when focused
        params.weight = hasFocus ? BITMOJI_CONTAINER_FOCUS_WEIGHT : 0;
        params.height = hasFocus ? 0 : mBitmojiContainerHeight;

        mBitmojiContainer.setLayoutParams(params);
    }

    @Override
    public void onBitmojiSelected(String imageUrl, Drawable previewDrawable) {
        handleBitmojiSend(imageUrl, previewDrawable);
    }




    private void handleBitmojiSend(String imageUrl, Drawable previewDrawable) {
        sendMessage(new ChatImageUrlMessage(true, imageUrl, previewDrawable));

        if(mBitmojiisSent == 0){
            sendDelayedMessage(new ChatTextMessage(false, "Oshey, Bitmoji Baad Gann!"), 500);
            sendDelayedMessage(new ChatImageMessage(false, R.drawable.looking_good), 1000);
        }else if(mBitmojiisSent == 1){
            sendDelayedMessage(new ChatImageMessage(false, R.drawable.party_time), 1000);
        }else if(mBitmojiisSent == 2){
            sendDelayedMessage(new ChatTextMessage(false, "lol"), 500);
        }else if(mBitmojiisSent == 14){
            sendDelayedMessage(new ChatImageMessage(false, R.drawable.chill), 1000);
        }

        mBitmojiisSent++;
    }

    private void sendDelayedMessage(final ChatMessage message, int delayMs) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessage(message);
            }
        }, delayMs);
    }
    private void defocusInput() {
        View currentFocus = getCurrentFocus();

        if (currentFocus == null) {
            return;
        }


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

        currentFocus.clearFocus();


    }
    private void loadExternalId() {
        SnapLogin.fetchUserData(this, EXTERNAL_ID_QUERY, null, new FetchUserDataCallback() {
            @Override
            public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                if (userDataResponse == null || userDataResponse.hasError()) {
                    return;
                }

                mMyExternalId = userDataResponse.getData().getMe().getExternalId();
                mFriendmojiToggle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(boolean b, int i) {
                //TODO: FAILED TO FETCH CURRENT USER. HANDLE ERROR.
            }
        });
    }
    private void sendText() {
        String text = mTextField.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        sendMessage(new ChatTextMessage(true, text));
        mTextField.setText("");
    }
    private void sendMessage(ChatMessage chatTextMessage) {
        mAdapter.add(chatTextMessage);
        mChatView.scrollToPosition(0);
    }
    private static int getRootViewHeightDiff(View view) {
        return view.getRootView().getHeight() - view.getHeight();
    }
    private void setBitmojiVisible(boolean isBitmojiVisible) {
        mIsBitmojiVisible = isBitmojiVisible;
        mBitmojiContainer.setVisibility(isBitmojiVisible ? View.VISIBLE : View.GONE);
    }

}
