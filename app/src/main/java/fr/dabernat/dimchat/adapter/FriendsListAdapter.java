package fr.dabernat.dimchat.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.dabernat.dimchat.R;
import fr.dabernat.dimchat.model.CurrentUser;
import fr.dabernat.dimchat.model.User;
import fr.dabernat.dimchat.utils.ImageConverter;

public class FriendsListAdapter extends BaseAdapter {

    private Context context;
    private List<User> userList;
    private LayoutInflater mLayoutInflater;
    private CurrentUser currentUser;

    public FriendsListAdapter(Context context, CurrentUser currentUser) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.currentUser = currentUser;
        userList = new ArrayList<>();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        Collections.reverse(userList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.adapter_friends, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvPseudo.setText(userList.get(position).getPseudo());
        String url = userList.get(position).getImageUrl();
        if(!url.isEmpty()) {
            String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
            String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("DimChat", Context.MODE_PRIVATE);
            Bitmap photoProfil = loadImageFromStorage(directory.getPath(), fileNameWithoutExtn);
            if( photoProfil != null) {
                photoProfil = ImageConverter.getRoundedCornerBitmap(photoProfil, 50);
                holder.ivProfil.setImageBitmap(photoProfil);
            } else {
                new DownloadImagesTask(fileNameWithoutExtn, holder.ivProfil).execute();
            }
        }

        return view;
    }

    public Bitmap getBitmapFromURL(String src, String fileName) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            saveToInternalStorage(myBitmap, fileName);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("DimChat", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, fileName + ".png");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private Bitmap loadImageFromStorage(String path, String fileName)
    {
        try {
            File f = new File(path, fileName + ".png");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }

    static class ViewHolder {

        ImageView ivProfil;
        TextView tvPseudo;

        public ViewHolder(View view) {
            ivProfil = (ImageView) view.findViewById(R.id.ivProfil);
            tvPseudo = (TextView) view.findViewById(R.id.tvPseudo);
        }

    }

    public class DownloadImagesTask extends AsyncTask<Void, Void, Bitmap> {

        String fileName;
        String url;
        ImageView imageView;

        public DownloadImagesTask(String fileName, ImageView imageView) {
            this.fileName = fileName;
            this.url = "http://www.raphaelbischof.fr/messaging/images/" + fileName + ".png";
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return getBitmapFromURL(url, fileName);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }


}