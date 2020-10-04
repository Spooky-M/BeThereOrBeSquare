package com.example.bethereorbesquare.repository;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveData;

import com.example.bethereorbesquare.dao.ColorDao;
import com.example.bethereorbesquare.entity.CustomColor;
import com.example.bethereorbesquare.network.GetColorService;
import com.example.bethereorbesquare.network.RetrofitInstance;
import com.example.bethereorbesquare.service.AppRoomDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ColorRepository {

    private ColorDao colorDao;
    private LiveData<List<CustomColor>> colors;

    public ColorRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getInstance(application);
        colorDao = db.colorDao();
        colors = colorDao.getAllColors();
    }

    /**
     * Obtains colors using {@link RetrofitInstance}. Fetches colors from
     * <a href="https://goo.gl/gEhgzs/">https://goo.gl/gEhgzs/</a>
     * and stores them with {@link ColorRepository} into Room database. On successful response
     * colors table is filled. While waiting for the response, {@link ProgressBar} is visible
     * and views in {@code viewsToDisable} are disabled.
     * @param context {@link Toast}'s context
     * @param progressBar {@link ProgressBar} to show while loading
     * @param viewsToDisable views to disable while loading
     * @return list of colors
     */
    public LiveData<List<CustomColor>> fetchColors(Context context, ProgressBar progressBar,
                                                    View... viewsToDisable) {
        for(View b : viewsToDisable) b.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        GetColorService service = RetrofitInstance.getRetrofitInstance().create(GetColorService.class);
        Call<List<CustomColor>> call = service.getColorData();

        call.enqueue(new Callback<List<CustomColor>>() {
            @Override
            public void onResponse(Call<List<CustomColor>> call, Response<List<CustomColor>> response) {
                List<CustomColor> colorsList = response.body();
                for(CustomColor c : colorsList) {
                    c.setName(c.getName().replaceAll("'", ""));
                }
                colorDao.insertAllColors(colorsList);
                colors = colorDao.getAllColors();

                for(View b : viewsToDisable) b.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<CustomColor>> call, Throwable t) {
                Toast.makeText(context,
                        "Something went wrong... Please try later!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        return colors;
    }

    public LiveData<List<CustomColor>> getAllColors() {
        return colors;
    }

    public @Nullable CustomColor getColor(String name) {
        List<CustomColor> colorList = colors.getValue();
        if(colors == null || colorList == null || colorList.isEmpty()) return null;
        for(CustomColor c : colorList) {
            if(c.getName().equals(name)) return c;
        }
        return null;
    }

    public void insertColor(CustomColor color) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> colorDao.insertColor(color));
    }

    public void insertAllColors(List<CustomColor> colorsList) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> colorDao.insertAllColors(colorsList));
    }

    public void deleteAllColors() {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> colorDao.deleteAllColors());

    }
}
