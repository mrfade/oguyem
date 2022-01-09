package dev.solak.oguyem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamedabulgasem.loadingoverlay.LoadingAnimation;
import com.mohamedabulgasem.loadingoverlay.LoadingOverlay;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.solak.oguyem.adapters.CommentsAdapter;
import dev.solak.oguyem.classes.API;
import dev.solak.oguyem.classes.User;
import dev.solak.oguyem.classes.Utils;
import dev.solak.oguyem.fragments.NewCommentDialogFragment;
import dev.solak.oguyem.models.Comment;
import dev.solak.oguyem.models.CommentsResponse;
import dev.solak.oguyem.models.Food;
import dev.solak.oguyem.models.Image;
import dev.solak.oguyem.models.InfoResponse;
import dev.solak.oguyem.models.Menu;
import dev.solak.oguyem.models.MenusResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuDayActivity extends AppCompatActivity implements NewCommentDialogFragment.NoticeDialogListener  {

    private int arrPosition;
    private Menu menu;
    private List<Comment> comments;
    private CommentsAdapter commentsAdapter;

    private File tempImageFile;
    private Uri outputFileUri;
    private Uri selectedImageUri;
    private String selectedImageUriPath;

    // display metrics
    int displayHeight, displayWidth;

    SwipeRefreshLayout swipeRefreshLayout;

    LinearLayout layout, innerLayout, menuLayout, commentsContentLayout, commentsListLayout;
    RelativeLayout newCommentLayout;
    CardView cardView, commentsCardView;
    TextView textViewDate, textViewMenuCalorie, textViewNoComment;
    ProgressBar progressBar;
//    ListView commentsListView;

    private final Map<Integer, int[]> imgDimCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_day);

        calculateDisplayMetrics();
        setWindowTransitions();

        // setup swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        setupSwipeRefresh();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        arrPosition = intent.getIntExtra("arrPosition", 0);
        menu = MenuWeekActivity.menu.get(arrPosition);

        layout = findViewById(R.id.layout);
        cardView = findViewById(R.id.card_view);
        innerLayout = findViewById(R.id.inner_layout);
        menuLayout = findViewById(R.id.menu_layout);
        textViewDate = findViewById(R.id.date);
        textViewMenuCalorie = findViewById(R.id.menuCalorie);

        commentsCardView = findViewById(R.id.comments_card_view);
        commentsContentLayout = findViewById(R.id.comments_content_layout);
        progressBar = findViewById(R.id.progressBar);
        textViewNoComment = findViewById(R.id.no_comment);
        newCommentLayout = findViewById(R.id.new_comment_layout);
        commentsListLayout = findViewById(R.id.comments_list_layout);
//        commentsListView = findViewById(R.id.comments_list);

        String formattedDate = Utils.formatTitleDate(menu.getDate());
        textViewDate.setText(formattedDate);
        String menuCalorie = Utils.calculateMenuCalorie(menu.getFoodList());
        textViewMenuCalorie.setText(String.format("Toplam kalori: %s", menuCalorie));

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int i = 0;
        List<Food> foodList = new ArrayList<Food>(menu.getFoodList());
        Collections.reverse(foodList);
        for (Food food : foodList) {
            View item = inflater.inflate(R.layout.fragment_menu_item, menuLayout, false);
            TextView name = item.findViewById(R.id.name);
            TextView calorie = item.findViewById(R.id.calorie);

            name.setText(food.getName());
            calorie.setText(food.getCalorie());

            menuLayout.addView(item, 0);
        }

        fetchCommentsFromApi();
        newCommentLayout.setVisibility(View.VISIBLE);
        /*if (menu.getDate().equals(Utils.formatDate("yyyy-MM-dd", new Date()))) {
            newCommentLayout.setVisibility(View.VISIBLE);
        } else {
            newCommentLayout.setVisibility(View.GONE);
        }*/
    }

    private void fetchCommentsFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        commentsListLayout.setVisibility(View.GONE);
        textViewNoComment.setVisibility(View.GONE);

        Call<CommentsResponse> call = API.apiService.getComments(menu.getDate());
        call.enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommentsResponse> call, @NonNull Response<CommentsResponse> response) {

                if (response.isSuccessful()) {
                    CommentsResponse commentsResponse = response.body();

                    comments = commentsResponse.getComments();

                    Log.d("CommentsFetch", "Success");

                    populateComments();

                    commentsListLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("CommentsFetch", "Failed - Request not successful!");
                    commentsFetchFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentsResponse> call, @NonNull Throwable t) {
                Log.d("CommentsFetch", "Failed - Request error!");
                commentsFetchFailed();
            }
        });
    }

    private void commentsFetchFailed() {
        Toast.makeText(MenuDayActivity.this, R.string.comment_fetch_failed, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    private void populateComments() {
//        commentsAdapter = new CommentsAdapter(comments, MenuDayActivity.this);
//        commentsListView.setAdapter(commentsAdapter);

        textViewNoComment.setVisibility(comments.size() > 0 ? View.GONE : View.VISIBLE);

        commentsListLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Comment comment : comments) {
            View item = inflater.inflate(R.layout.fragment_comment_item, commentsListLayout, false);
            RatingBar rating = item.findViewById(R.id.rating);
            TextView timeago = item.findViewById(R.id.timeago);
            TextView tComment = item.findViewById(R.id.comment);
            LinearLayout imagesLayout = item.findViewById(R.id.images_layout);

            Log.d("COMMENT", "User.id: " + User.user.getId());
            Log.d("COMMENT", "Comment.user.id: " + comment.getUserId());

            Button deleteButton = item.findViewById(R.id.delete_comment);
            if (comment.getUserId().equals(User.user.getId())) {
                item.setBackgroundColor(Color.parseColor("#515151"));

                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuDayActivity.this, R.style.AlertDialogTheme);
                    // Add the buttons
                    builder.setPositiveButton(R.string.yes, (dialog, id) -> {
                        // User clicked OK button

                        // delete comment
                        Call<InfoResponse> call = API.apiService.deleteComment(menu.getDate(), comment.getId());
                        call.enqueue(new Callback<InfoResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<InfoResponse> call, @NonNull Response<InfoResponse> response) {

                                if (response.isSuccessful()) {
                                    Log.d("COMMENT_DELETE", "Success");

                                    // fetch comments
                                    fetchCommentsFromApi();

                                    Toast.makeText(MenuDayActivity.this, getString(R.string.comment_deleted_successfully), Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("COMMENT_DELETE", "Failed - Request not successful!");
                                    Toast.makeText(MenuDayActivity.this, getString(R.string.comment_deletion_failed), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<InfoResponse> call, @NonNull Throwable t) {
                                Log.d("CommentsFetch", "Failed - Request error!");
                                Toast.makeText(MenuDayActivity.this, getString(R.string.comment_deletion_failed), Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                    builder.setNegativeButton(R.string.no, (dialog, id) -> {
                        // User cancelled the dialog
                    });

                    builder.setTitle("Silmek istediğine emin misin?");

                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }

            try {
                rating.setRating(Float.parseFloat(comment.getRating()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                PrettyTime p = new PrettyTime();
                timeago.setText(p.format(Utils.parseDate("yyyy-MM-dd'T'HH:mm:ss.uuuu'Z'", comment.getCreatedAt())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tComment.setText(comment.getComment());

            for (Image image : comment.getImages()) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(Utils.dpToPx(80), Utils.dpToPx(80)));
                imageView.setOnClickListener(view -> onMenuCommentImageClick(image));

                imagesLayout.addView(imageView);
                imagesLayout.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(image.getUrl())
                        .fit()
                        .centerCrop()
                        .into(imageView);
            }

            commentsListLayout.addView(item);
        }
    }

    private void setWindowTransitions() {
        /*// here we are initializing
        // fade animation.
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        // here also we are excluding status bar,
        // action bar and navigation bar from animation.
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        // below methods are used for adding
        // enter and exit transition.
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);*/

        Slide slide = new Slide();
        slide.setDuration(300);
        slide.setSlideEdge(Gravity.BOTTOM);

        View decor = getWindow().getDecorView();

        slide.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);

        /*getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);*/
        getWindow().setAllowEnterTransitionOverlap(false);

        ChangeBounds changeBounds = new ChangeBounds();

        getWindow().setSharedElementEnterTransition(changeBounds);
        getWindow().setSharedElementExitTransition(changeBounds);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Call<CommentsResponse> call = API.apiService.getComments(menu.getDate());
                call.enqueue(new Callback<CommentsResponse>() {
                    @Override
                    public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {

                        if (response.isSuccessful()) {
                            CommentsResponse commentsResponse = response.body();

                            comments = commentsResponse.getComments();

                            Log.d("CommentsFetch", "Success");

                            populateComments();
                        } else {
                            Log.d("CommentsFetch", "Failed - Request not successful!");
                            commentsFetchFailed();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<CommentsResponse> call, Throwable t) {
                        Log.d("CommentsFetch", "Failed - Request error!");
                        commentsFetchFailed();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });

        // Scheme colors for animation
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright, getTheme()),
                getResources().getColor(android.R.color.holo_green_light, getTheme()),
                getResources().getColor(android.R.color.holo_orange_light, getTheme()),
                getResources().getColor(android.R.color.holo_red_light, getTheme())
        );
    }

    public void newCommentBtnClick(View view) {
        DialogFragment newFragment = new NewCommentDialogFragment();
        newFragment.show(getSupportFragmentManager(), "new_comment");

        tempImageFile = null;
        outputFileUri = selectedImageUri = null;
        selectedImageUriPath = null;
    }

    public void onSelectImageClick() {
        // check permission
//        if (!Utils.checkReadStoragePermission(MenuDayActivity.this) || !Utils.checkCameraPermission(MenuDayActivity.this)) {
        if (!Utils.checkReadStoragePermission(MenuDayActivity.this)) {
            requestPermissions( new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1212);
            requestPermissions( new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1213);
//            requestPermissions( new String[] { Manifest.permission.CAMERA }, 1214);
        } else {
            // has permission
            openImageIntent();
        }
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
//        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
//        root.mkdirs();
//        final String fname = Utils.getUniqueImageFilename();
//        final File sdImageMainDirectory = new File(root, fname);
//        File outputDir = this.getCacheDir(); // context being the Activity pointer
//        File sdImageMainDirectory = null;
//        try {
//            sdImageMainDirectory = File.createTempFile(fname, ".jpg", outputDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        File root_dir;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            root_dir = getExternalFilesDir(null);
        } else {
            root_dir = getCacheDir();
        }
//        root_dir = getCacheDir();

        File root =  new File(root_dir, File.separator + "MyDir" + File.separator);
        if (root.mkdirs()) {
            Log.d("SELECT_IMAGE", "root directory created");
        } else {
            Log.d("SELECT_IMAGE", "root directory failed to be created");
        }

        final String tempFileName = Utils.getUniqueImageFilename();
        tempImageFile = new File(root, tempFileName);

        outputFileUri = Uri.fromFile(tempImageFile);
        Log.d("SELECT_IMAGE", "outputFileUri: " + Utils.getPath(this, outputFileUri));

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, 1111);
    }

    private void onMenuCommentImageClick(Image image) {
        // https://stackoverflow.com/a/7694150/10873011
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.dialog_image, null);

        int imageViewMargin = 100;
        ImageView imageView = view.findViewById(R.id.image);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(displayWidth - imageViewMargin * 2, displayHeight - imageViewMargin * 2);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
//        imageView.setLayoutParams(layoutParams);

        Button button = view.findViewById(R.id.close);
        button.setOnClickListener(view1 -> dialog.dismiss());

        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        dialog.setContentView(view);
        dialog.show();

        // dimensions witdh, height
        final int[] dims = { 0, 0 };
        Picasso.get()
                .load(image.getUrl())
                // https://gist.github.com/lenamuit/9ae1458b43fc2cf28db2
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {

                        int maxWidth = displayWidth - imageViewMargin * 2;
                        int maxHeight = displayHeight - imageViewMargin * 2;

                        Log.d("COMMENT_IMAGE", String.valueOf(maxWidth));
                        Log.d("COMMENT_IMAGE", String.valueOf(maxHeight));

                        dims[0] = maxWidth;
                        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                        dims[1] = (int) (dims[0] * aspectRatio);

                        Log.d("COMMENT_IMAGE", String.valueOf((double) source.getWidth()));
                        Log.d("COMMENT_IMAGE", String.valueOf((double) source.getHeight()));
                        Log.d("COMMENT_IMAGE", String.valueOf(aspectRatio));
                        Log.d("COMMENT_IMAGE", String.valueOf(dims[0]));
                        Log.d("COMMENT_IMAGE", String.valueOf(dims[1]));

                        // if image is too tall then recalculate dims
                        if (dims[1] > maxHeight) {
                            dims[1] = maxHeight;
                            dims[0] = (int) (dims[1] / aspectRatio);

                            Log.d("COMMENT_IMAGE", "targetHeight > maxHeight");
                            Log.d("COMMENT_IMAGE", String.valueOf(dims[0]));
                            Log.d("COMMENT_IMAGE", String.valueOf(dims[1]));
                        }

                        // cache dims because transform calls callback once
                        imgDimCache.put(image.getId(), dims);

                        if (dims[1] <= 0 || dims[0] <= 0) return source;
                        Bitmap result = Bitmap.createScaledBitmap(source, dims[0], dims[1], false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    }

                    @Override
                    public String key() {
                        return "key";
                    }
                })
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        int[] dims = imgDimCache.get(image.getId());
                        Log.d("COMMENT_IMAGE", "image load callback");

                        progressBar.setVisibility(View.GONE);

                        if (dims != null) {
                            Log.d("COMMENT_IMAGE", String.valueOf(dims[0]));
                            Log.d("COMMENT_IMAGE", String.valueOf(dims[1]));

                            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(dims[0], dims[1]);
                            imageView.setLayoutParams(layoutParams2);
                        } else {
                            Log.d("COMMENT_IMAGE", "dims array is null");
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private void deleteTempImageFile() {
        if (tempImageFile != null && tempImageFile.exists()) {
            if (tempImageFile.delete()) {
                Log.d("SELECT_IMAGE", "temp file deleted");
            } else {
                Log.d("SELECT_IMAGE", "temp file failed to be deleted");
            }
        }
    }

    // https://stackoverflow.com/a/4744499/10873011
    private void calculateDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;
    }

    // https://developer.android.com/training/permissions/requesting
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1212:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.

//                    openImageIntent();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.

                    Toast.makeText(MenuDayActivity.this, "Dosya okuma izni vermelisiniz", Toast.LENGTH_LONG).show();
                }
                return;

            case 1213:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.

//                    openImageIntent();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.

                    Toast.makeText(MenuDayActivity.this, "Dosya yazma izni vermelisiniz", Toast.LENGTH_LONG).show();
                }
                return;

            case 1214:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.

//                    openImageIntent();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.

                    Toast.makeText(MenuDayActivity.this, "Kamera izni vermelisiniz", Toast.LENGTH_LONG).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("SELECT_IMAGE", "" + (resultCode == RESULT_OK) + " " + requestCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1111) {
                final boolean isCamera;
                if (data == null || data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                    }
                }

                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                selectedImageUriPath = Utils.getPath(this, selectedImageUri);
                Log.d("SELECT_IMAGE", "" + selectedImageUriPath);

                FragmentManager fm = getSupportFragmentManager();
                NewCommentDialogFragment fragment = (NewCommentDialogFragment)fm.findFragmentByTag("new_comment");
                ImageView imageView = (ImageView) fragment.view.findViewById(R.id.image);
                imageView.setImageURI(selectedImageUri);
            }
        } else {
            // remove temp file if exists
            deleteTempImageFile();
        }
    }

    // https://developer.android.com/guide/topics/ui/dialogs#PassingEvents
    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(NewCommentDialogFragment dialog) {
        // User touched the dialog's positive button

        String rating = dialog.getRating();
        String comment = dialog.getComment();

        if (rating.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, getString(R.string.new_comment_fillfields), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("SELECT_IMAGE", "" + selectedImageUriPath);

        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain");

        // https://stackoverflow.com/a/36657336/10873011
        Map<String, RequestBody> map = new HashMap<>();
        map.put("comment", RequestBody.create(comment, MEDIA_TYPE_PLAIN));
        map.put("rating", RequestBody.create(rating, MEDIA_TYPE_PLAIN));

        if (selectedImageUri != null) {
            File file = new File(selectedImageUriPath);
            RequestBody fileBody = RequestBody.create(file, MEDIA_TYPE_JPG);
            map.put("image\"; filename=\"pp.jpg", fileBody);
        }

        LoadingOverlay loadingOverlay = LoadingOverlay.Companion.with(this, LoadingAnimation.BuiltinAnimations.getPROGRESS_BAR(), 0.8f, false, null, null, null);
        loadingOverlay.show();

        Call<Comment> call = API.apiService.newComment(menu.getDate(), map);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                int statusCode = response.code();
                Log.d("COMMENT", "status code" + statusCode);
                if (statusCode == 201) {
                    Log.d("COMMENT", "new comment success");
                    Comment comment = response.body();

                    // remove temp file if exists
                    deleteTempImageFile();

                    // hide loader
                    loadingOverlay.dismiss();

                    // success info
                    Toast.makeText(MenuDayActivity.this, getString(R.string.new_comment_success), Toast.LENGTH_LONG).show();

                    // reload comments
                    fetchCommentsFromApi();
                } else {
                    // error info
                    Toast.makeText(MenuDayActivity.this, getString(R.string.new_comment_failed), Toast.LENGTH_LONG).show();

                    // remove temp file if exists
                    deleteTempImageFile();

                    // hide loader
                    loadingOverlay.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                // Log error here since request failed
                Log.d("COMMENT", "new comment failed");
                t.printStackTrace();

                // error info
                Toast.makeText(MenuDayActivity.this, getString(R.string.new_comment_failed), Toast.LENGTH_LONG).show();

                // remove temp file if exists
                deleteTempImageFile();

                // hide loader
                loadingOverlay.dismiss();
            }
        });
    }

    @Override
    public void onDialogNegativeClick(NewCommentDialogFragment dialog) {
        // User touched the dialog's negative button

        // remove temp file if exists
        deleteTempImageFile();
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finishAfterTransition();
    }
}