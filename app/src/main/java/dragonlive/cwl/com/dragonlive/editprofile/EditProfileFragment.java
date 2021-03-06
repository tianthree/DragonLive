package dragonlive.cwl.com.dragonlive.editprofile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mysdk.glide.GlideUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;
import dragonlive.cwl.com.dragonlive.MainActivity;
import dragonlive.cwl.com.dragonlive.R;
import dragonlive.cwl.com.dragonlive.application.MyApplication;
import dragonlive.cwl.com.dragonlive.util.PicChooseHelper;

/**
 * Created by cwl on 2018/4/23.
 */

public class EditProfileFragment extends android.support.v4.app.Fragment {
    @Bind(R.id.title_bar)
    Toolbar mTitlebar;
    @Bind(R.id.avatar_img)
    ImageView mAvatarView;
    @Bind(R.id.avatar)
    LinearLayout mAvatar;
    @Bind(R.id.nick_name)
    ProfileEdit mNickNameEdt;
    @Bind(R.id.gender)
    ProfileEdit mGenderEdt;
    @Bind(R.id.sign)
    ProfileEdit mSignEdt;
    @Bind(R.id.renzheng)
    ProfileEdit mRenzhengEdt;
    @Bind(R.id.location)
    ProfileEdit mLocationEdt;
    @Bind(R.id.id)
    ProfileTextView  mIdView;
    @Bind(R.id.level)
    ProfileTextView mLevelView;
    @Bind(R.id.get_nums)
    ProfileTextView mGetNumsView;
    @Bind(R.id.send_nums)
    ProfileTextView mSendNumsView;
    @Bind(R.id.complete)
    Button mCompleteBtn;
    @Bind(R.id.activity_edit_profile)
    LinearLayout activityEditProfile;
    @Bind(R.id.share_view)
    ProfileTextView mShareView;
    private TIMUserProfile mUserProfile;
    private PicChooseHelper mPicChooserHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);
        setListeners();
        setTitleBar();
        setIconKey();//设置字段和icon
        getSelfInfo();
        return view;
    }



    private void setTitleBar() {
        mTitlebar.setTitle("编辑个人信息");
        mTitlebar.setTitleTextColor(Color.WHITE);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(mTitlebar);
        }
    }

    private void setIconKey() {
        mNickNameEdt.set(R.drawable.ic_info_nickname, "昵称", "");
        mGenderEdt.set(R.drawable.ic_info_gender, "性别", "");
        mSignEdt.set(R.drawable.ic_info_sign, "签名", "无");
        mRenzhengEdt.set(R.drawable.ic_info_renzhen, "认证", "未知");
        mLocationEdt.set(R.drawable.ic_info_location, "地区", "未知");
        mIdView.set(R.drawable.ic_info_id, "ID", "");
        mLevelView.set(R.drawable.ic_info_level, "等级", "0");
        mGetNumsView.set(R.drawable.ic_info_get, "获得票数", "0");
        mSendNumsView.set(R.drawable.ic_info_send, "送出票数", "0");
        mShareView.set(R.drawable.share_app,"分享",null);
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                MyApplication.getApplication().setSelfProfile(timUserProfile);
                mUserProfile = timUserProfile;
                updateViews(timUserProfile);
            }
        });
    }

    private void updateViews(TIMUserProfile timUserProfile) {
//更新界面
        String faceUrl = timUserProfile.getFaceUrl();
        if (TextUtils.isEmpty(faceUrl)) {
           GlideUtil.loadLocalImage(getActivity(),R.drawable.default_avatar,mAvatarView);
        } else {
           GlideUtil.loadImageCircle(getActivity(),timUserProfile.getFaceUrl(),mAvatarView,50);
        }
        mNickNameEdt.updateValue(timUserProfile.getNickName());
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        mGenderEdt.updateValue(genderStr);
        mSignEdt.updateValue(timUserProfile.getSelfSignature());
        mLocationEdt.updateValue(timUserProfile.getLocation());
        mIdView.updateValue(timUserProfile.getIdentifier());

        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
        mRenzhengEdt.updateValue(getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知"));
        mLevelView.updateValue(getValue(customInfo, CustomProfile.CUSTOM_LEVEL, "0"));
        mGetNumsView.updateValue(getValue(customInfo, CustomProfile.CUSTOM_GET, "0"));
        mSendNumsView.updateValue(getValue(customInfo, CustomProfile.CUSTOM_SEND, "0"));
    }
    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueBytes = customInfo.get(key);
            if (valueBytes != null) {
                return new String(valueBytes);
            }
        }
        return defaultValue;
    }
    private void setListeners() {
        mAvatar.setOnClickListener(clickListener);
        mNickNameEdt.setOnClickListener(clickListener);
        mGenderEdt.setOnClickListener(clickListener);
        mSignEdt.setOnClickListener(clickListener);
        mRenzhengEdt.setOnClickListener(clickListener);
        mLocationEdt.setOnClickListener(clickListener);
        mCompleteBtn.setOnClickListener(clickListener);
        mShareView.setOnClickListener(clickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.avatar:
                    //修改头像
                    choosePic();
                    break;
                case R.id.nick_name:
                    //修改昵称
                    showEditNickNameDialog();
                    break;
                case R.id.gender:
                    //修改性别
                    showEditGenderDialog();
                    break;
                case R.id.sign:
                    //修改签名
                    showEditSignDialog();
                    break;
                case R.id.renzheng:
                    //修改认证
                    showEditRenzhengDialog();
                    break;
                case R.id.location:
                    //修改位置
                    showEditLocationDialog();
                    break;
                case R.id.share_view:
                    showShare();
                    break;
                case R.id.complete:
                    //完成，点击跳转到主界面
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void showEditLocationDialog() {
        EditStrProfileDialog dialog = new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setLocation(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新地区失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("地区", R.drawable.ic_info_location, mLocationEdt.getValue());
    }

    private void showEditRenzhengDialog() {
        EditStrProfileDialog dialog = new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_RENZHENG, content.getBytes(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新认证失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("认证", R.drawable.ic_info_renzhen, mRenzhengEdt.getValue());
    }

    private void showEditSignDialog() {
        EditStrProfileDialog dialog=new EditStrProfileDialog(getActivity());
       dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
           @Override
           public void onOk(String title, String content) {
               TIMFriendshipManager.getInstance().setSelfSignature(content, new TIMCallBack() {
                   @Override
                   public void onError(int i, String s) {
                       Toast.makeText(getActivity(), "更新签名失败：" + s, Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onSuccess() {
                       //更新成功
                      getSelfInfo();
                   }
               });
           }
       });
        dialog.show("签名", R.drawable.ic_info_sign, mSignEdt.getValue());
    }


    private void showEditGenderDialog() {
   EditGenderDialog dialog=new EditGenderDialog(getActivity());
        dialog.setOnChangeGenderListener(new EditGenderDialog.OnChangeGenderListener() {
            @Override
            public void onChangeGender(boolean isMale) {
                TIMFriendGenderType gender = isMale ? TIMFriendGenderType.Male : TIMFriendGenderType.Female;
                TIMFriendshipManager.getInstance().setGender(gender, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新性别失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show(true);
    }

    private void showEditNickNameDialog() {
        EditStrProfileDialog dialog = new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setNickName(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新昵称失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("昵称", R.drawable.ic_info_nickname, mNickNameEdt.getValue());
    }

    private void choosePic() {
        Log.i("info1", "choosePic: ");
      if (mPicChooserHelper==null){
          mPicChooserHelper=new PicChooseHelper(this, PicChooseHelper.PicType.Avatar);
         mPicChooserHelper.setOnChooseResultListener(new PicChooseHelper.OnChooseResultListener() {
             @Override
             public void onSuccess(String url) {
                 Log.i("info1", "onSuccess: "+url);
                 updateAvatar(url);
             }

             @Override
             public void onFail(String msg) {
                 //Log.i("info1", "onfail: "+msg);
                 Toast.makeText(getActivity(), "头像更新失败", Toast.LENGTH_SHORT).show();
             }
         });
      }
      mPicChooserHelper.showPicChooseDialog();
    }

    private void updateAvatar(String url) {
        TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "头像更新失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                //更新成功
                  getSelfInfo();
            }
        });
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(getString(R.string.share));
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("dragonlive互动直播");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
       oks.setImageUrl(MyApplication.getApplication().getSelfProfile().getFaceUrl()); //网络图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment(MyApplication.getApplication().getSelfProfile().getNickName()+"的直播间");
        // 启动分享GUI
        oks.show(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPicChooserHelper!=null){
            mPicChooserHelper.onActivityResult(requestCode,resultCode,data);
        }else
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (mPicChooserHelper!=null)
        mPicChooserHelper.diaglogDismiss();
        super.onDestroy();

    }
}
