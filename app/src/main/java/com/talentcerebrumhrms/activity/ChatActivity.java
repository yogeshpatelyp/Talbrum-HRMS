package com.talentcerebrumhrms.activity;

/**
 * Created by saransh on 19-11-2016.
 * chat bot acivity which is a swipable activity i.e. can be swiped to close the activity.
 */

public class ChatActivity  {
   /* Toolbar toolbar;
    EditText type_message;
    LinearLayout chat_send;
    ListView chat_list;
    ChatAdapter chatAdapter;
    ArrayList<ChatDataType> dataArray;
    ImageButton go_to_top;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.tool_bar_chat);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.chat);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        @SuppressLint("PrivateResource")
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        dataArray = AppController.chat_data_array;
        type_message = (EditText) findViewById(R.id.type_message);
        chat_send = (LinearLayout) findViewById(R.id.chat_send);
        chat_list = (ListView) findViewById(R.id.listview_chat);
        go_to_top = (ImageButton) findViewById(R.id.go_to_top);
        chatAdapter = new ChatAdapter(this, dataArray);
        chat_list.setAdapter(chatAdapter);

        go_to_top.setVisibility(View.GONE);
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type_message.getText().toString().trim().equalsIgnoreCase("")) {
                    ChatDataType temp = new ChatDataType();
                    temp.setChat(type_message.getText().toString());
                    temp.setSenderType(1);
                    AppController.chat_data_array.add(temp);
                    chatAdapter.updateAdapter(AppController.chat_data_array);
                  //  AppController.chat_bot(ChatActivity.this, ChatActivity.this, type_message.getText().toString());
                    type_message.setText("");
                }
            }
        });
        go_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat_list.smoothScrollToPosition(0);
            }
        });


        chat_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (chat_list.getFirstVisiblePosition() == 0)
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                else
                    view.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });


        chat_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                Log.e("onscroll", "true");
                if (i > 1)
                    go_to_top.setVisibility(View.VISIBLE);
                else {
                    go_to_top.setVisibility(View.GONE);
                }
            }
        });
        setDragEdge(SwipeBackLayout.DragEdge.TOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("backpressed", "true");
        super.onBackPressed();

    }

    public void updateadapter() {
        dataArray = new ArrayList<>(AppController.chat_data_array);
        chatAdapter.updateAdapter(dataArray);
        chat_list.setSelection(chatAdapter.getCount() - 1);


    }*/

}
