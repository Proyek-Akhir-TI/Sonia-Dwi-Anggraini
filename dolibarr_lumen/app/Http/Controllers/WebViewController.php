<?php
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class WebViewController extends Controller
{
    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        //
    }

    public function getPesananTokoDetail(Request $request)
    {
        $pesanan_id = $request->pesanan_id;

        $pesanan = DB::select('SELECT a.rowid AS id,
                                        a.fk_product AS produk_id,
                                        b.label AS nama_produk,
                                        a.qty AS qty,
                                        a.price AS harga,
                                        ROUND(a.total_ht,2) AS subtotal
                                FROM llx_commandedet a
                                LEFT JOIN llx_product b ON a.fk_product = b.rowid
                                WHERE a.fk_commande=?', [$pesanan_id]);

        return view('webview.pesanan_detail', ['pesananList' => $pesanan]);
    }

}
