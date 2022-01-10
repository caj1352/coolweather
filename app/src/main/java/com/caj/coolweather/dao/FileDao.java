package com.caj.coolweather.dao;

import com.caj.coolweather.CoolWeatherApplication;
import com.caj.coolweather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class FileDao {
    private static FileDao INSTANCE = null;
    private FileDao() {

    }

    public interface CallBack {
        void onSuccess(String response);
        void onFail(Exception e);
    }

    public static FileDao getInstance() {
        if (INSTANCE == null) {
            synchronized (FileDao.class) {
                INSTANCE = new FileDao();
            }
        }
        return INSTANCE;
    }

    /**
     * 从文件获取省数据
     * @param callBack
     */
    public void getProvincesFromFile(CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> savedProvinceSet= new HashSet<>();
                JSONArray provinces = new JSONArray();
                InputStream in = null;
                BufferedReader reader = null;
                try {

                    in = CoolWeatherApplication.getInstance().getResources()
                            .openRawResource(R.raw.code_data);
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    do {
                        line = reader.readLine();
                        // 下标0是code，下标1是镇名，下标2是市名，下标3是省名
                        String[] params = line.split(",");
                        // 如果不是保存过的省名
                        if (!savedProvinceSet.contains(params[3])) {

                            // 创建省的数据
                            JSONObject province = new JSONObject();
                            province.put("provinceName", params[3]);

                            // 添加到返回结果
                            provinces.put(province);

                            // 记录已经保存过的省名
                            savedProvinceSet.add(params[3]);
                        }
                    } while (line != null);
                    if (callBack != null) {
                        callBack.onSuccess(provinces.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onFail(e);
                    }
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 从文件获取市数据
     * @param provinceName
     * @param callBack
     */
    public void getCitiesFromFile(String provinceName, CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> savedCitySet= new HashSet<>();
                JSONArray cities = new JSONArray();
                InputStream in = null;
                BufferedReader reader = null;
                try {
                    in = CoolWeatherApplication.getInstance().getResources()
                            .openRawResource(R.raw.code_data);
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    do {
                        line = reader.readLine();
                        // 下标0是code，下标1是镇名，下标2是市名，下标3是省名
                        String[] params = line.split(",");

                        // 如果是所在的省
                        if (provinceName == params[3]) {
                            // 如果不是保存过的市名
                            if (!savedCitySet.contains(params[2])) {

                                // 创建市的数据
                                JSONObject city = new JSONObject();
                                city.put("provinceName", provinceName);
                                city.put("cityName", params[2]);

                                // 添加到返回结果
                                cities.put(city);

                                // 记录已经保存过的市名
                                savedCitySet.add(params[2]);
                            }
                        }

                    } while (line != null);
                    if (callBack != null) {
                        callBack.onSuccess(cities.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onFail(e);
                    }
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    /**
     * 从文件获取镇数据
     * @param provinceName
     * @param cityName
     * @param callBack
     */
    public void getCountriesFromFile(String provinceName, String cityName, CallBack callBack) {
        Set<String> savedCountrySet= new HashSet<>();
        JSONArray countries = new JSONArray();
        InputStream in = null;
        BufferedReader reader = null;
        try {
            in = CoolWeatherApplication.getInstance().getResources()
                    .openRawResource(R.raw.code_data);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            do {
                line = reader.readLine();
                // 下标0是code，下标1是镇名，下标2是市名，下标3是省名
                String[] params = line.split(",");

                // 如果是所在的省和市
                if (provinceName == params[3] && cityName == params[2]) {
                    // 如果不是保存过的镇名
                    if (!savedCountrySet.contains(params[1])) {

                        // 保存镇对象
                        JSONObject country = new JSONObject();
                        country.put("provinceName", provinceName);
                        country.put("cityName", cityName);
                        country.put("countryName", params[1]);
                        country.put("code", params[0]);

                        // 添加到返回结果
                        countries.put(country);

                        // 记录已经保存过的镇名
                        savedCountrySet.add(params[1]);
                    }
                }

            } while (line != null);
            if (callBack != null) {
                callBack.onSuccess(countries.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFail(e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
