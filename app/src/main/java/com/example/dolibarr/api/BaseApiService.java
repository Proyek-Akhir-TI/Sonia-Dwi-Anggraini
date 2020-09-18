package com.example.dolibarr.api;

import com.example.dolibarr.ui.kegiatan.KegiatanModelList;
import com.example.dolibarr.ui.kegiatan_detail.KegiatanDetailModelList;
import com.example.dolibarr.ui.pengantaran.PengantaranModelList;
import com.example.dolibarr.ui.pesanan.PesananModelList;
import com.example.dolibarr.ui.presensi.PresensiModelList;
import com.example.dolibarr.ui.produk.ProdukModelList;
import com.example.dolibarr.ui.toko.TokoModelList;
import com.google.android.gms.common.api.GoogleApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BaseApiService {
    /**
     * @param username
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("post-login")
    Call<ResponseBody> postLogin(@Field("username") String username,
                                 @Field("password") String password);

    /**
     * @param user_id
     * @param token_id
     * @return
     */
    @FormUrlEncoded
    @POST("post-tokenid")
    Call<ResponseBody> postTokenId(@Field("user_id") int user_id,
                                   @Field("token_id") String token_id);


    @FormUrlEncoded
    @POST("post-presensi")
    Call<ResponseBody> postPresensi(@Field("user_id") int user_id,
                                    @Field("foto") String foto,
                                    @Field("longitude") String longitude,
                                    @Field("latitude") String latitude,
                                    @Field("keterangan") String keterangan,
                                    @Field("status") String status
    );

    /**
     * mengambil data profile toko
     *
     * @param toko_id int toko_id
     * @return none
     */
    @GET("get-toko-profile")
    Call<ResponseBody> getTokoProfile(@Query("toko_id") int toko_id);

    /**
     * mengupdate atau insert data toko
     *
     * @param toko_id   int , jika bernilai 0, maka new row jika lebih besar maka update
     * @param nama      string, nama toko
     * @param alamat    string, alamat toko
     * @param kota      string, kota
     * @param latitude
     * @param longitude
     * @return
     */
    @FormUrlEncoded
    @POST("post-toko-profile")
    Call<ResponseBody> postTokoProfile(@Field("toko_id") int toko_id,
                                       @Field("nama") String nama,
                                       @Field("alamat") String alamat,
                                       @Field("kota") String kota,
                                       @Field("foto") String foto,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude);


    /**
     * mengambil list toko
     * link : http://localhost/dolibarr/htdocs/societe/list.php?leftmenu=thirdparties
     * tabel : llx_societe
     *
     * @return TokoModelList
     */
    @GET("get-toko-list")
    Call<TokoModelList> getTokoList(@Query("filter") String filter);

    @GET("get-pesanan-toko")
    Call<PesananModelList> getPesananTokoList(@Query("toko_id") int toko_id);

    @GET("get-pengantaran")
    Call<PengantaranModelList> getPengantaranList();

    @GET("delete-pesanan-toko")
    Call<ResponseBody> deletePesananToko(@Query("pesanan_id") int pesanan_id);

    @GET("get-produk-list")
    Call<ProdukModelList> getProdukList(@Query("so_id") int so_id, @Query("filter") String filter);

    @GET("delete-toko")
    Call<ResponseBody> deleteToko(@Query("toko_id") int toko_id);

    @FormUrlEncoded
    @POST("post-pesanan-baru")
    Call<ResponseBody> postPesananBaru(@Field("user_id") int user_id,
                                       @Field("toko_id") int toko_id);

    @FormUrlEncoded
    @POST("post-update-pesanan")
    Call<ResponseBody> updatePesanan(@Field("so_id") int so_id,
                                     @Field("produk_id") int produk_id,
                                     @Field("qty") int etQty);

    @GET("get-kegiatan-list")
    Call<KegiatanModelList> getKegiatanList(@Query("user_id") int user_id);

    @FormUrlEncoded
    @POST("post-kegiatan")
    Call<ResponseBody> postKegiatan(@Field("user_id") int user_id,
                                    @Field("foto") String foto,
                                    @Field("longitude") String longitude,
                                    @Field("latitude") String latitude,
                                    @Field("keterangan") String keterangan
    );

    @GET("get-kegiatan-detail-list")
    Call<KegiatanDetailModelList> getKegiatanDetailList(@Query("user_id") int user_id,
                                                        @Query("tanggal") String tanggal);

    @GET("get-presensi-list")
    Call<PresensiModelList> getPresensiList(@Query("user_id") int user_id);

    @GET("get-status-presensi")
    Call<ResponseBody> getStatusPresensi(@Query("user_id") int user_id);


//    @FormUrlEncoded
//    @POST("post-foto-penyerahan")
//    Call<ResponseBody> postFotoPenyerahan(@Field("pesanan_id") int pesanan_id,@Field("foto") String foto);


    @FormUrlEncoded
    @POST("post-pesanan-selesai")
    Call<ResponseBody> postPesananSelesai(@Field("pesanan_id") int pesanan_id,@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("post-toko-profile")
    Call<ResponseBody> postTokoProfileTanpaFoto(@Field("toko_id") int toko_id,
                                       @Field("nama") String nama,
                                       @Field("alamat") String alamat,
                                       @Field("kota") String kota,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("post-foto-penyerahan")
    Call<ResponseBody> postFotoPenyerahan(@Field("pesanan_id") int pesanan_id, @Field("foto") String foto);
}
