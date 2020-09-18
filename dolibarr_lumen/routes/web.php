<?php

/** @var \Laravel\Lumen\Routing\Router $router */

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It is a breeze. Simply tell Lumen the URIs it should respond to
| and give it the Closure to call when that URI is requested.
|
 */

$router->get('/', function () use ($router) {
    return $router->app->version();
});

$router->group(['prefix' => 'api'], function () use ($router) {
    $router->post('post-login', 'ApiController@postLogin');
    $router->post('post-presensi', 'ApiController@postPresensi');
    $router->get('get-status-presensi', 'ApiController@getStatusPresensi');
    $router->get('get-presensi-list', 'ApiController@getPresensiList');
    #
    $router->post('post-tokenid', 'ApiController@postTokenId');
    #
    $router->get('get-toko-list', 'ApiController@getTokoList');
    $router->get('get-toko-profile', 'ApiController@getTokoProfile');
    $router->post('post-toko-profile', 'ApiController@postTokoProfile');
    $router->get('delete-toko', 'ApiController@deleteToko');
    #
    $router->get('get-produk-list', 'ApiController@getProdukList');
    #
    $router->get('get-pesanan-toko', 'ApiController@getPesananToko');
    $router->post('post-pesanan-baru', 'ApiController@postPesananBaru');
    $router->get('delete-pesanan-toko', 'ApiController@deletePesananToko');
    $router->post('post-update-pesanan', 'ApiController@postUpdatePesanan');
    // $router->get('get-pesanan-toko-detail', 'ApiController@getPesananTokoDetail');
    #
    $router->get('get-file', 'ApiController@getFile');

    #
    $router->get('get-kegiatan-list', 'ApiController@getkegiatanList');
    $router->get('get-kegiatan-detail-list', 'ApiController@getKegiatanDetailList');
    $router->post('post-kegiatan', 'ApiController@postKegiatan');

    #
    $router->get('get-pengantaran','ApiController@getPengantaran');

    #
    $router->post('post-pesanan-selesai','ApiController@postPesananSelesai');

    #
    $router->post('post-foto-penyerahan','ApiController@postFotoPenyerahan');
});

$router->group(['prefix' => 'webview'], function () use ($router) {

    $router->get('get-pesanan-toko-detail', 'WebViewController@getPesananTokoDetail');

});
