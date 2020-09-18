<?php

namespace App\Http\Controllers;

use App\Facture;
use App\FactureDetails;
use App\FactureDetailsRec;
use App\FactureRec;
use App\Kegiatan;
use App\Presensi;
use App\Propal;
use App\PropalDetails;
use App\SalesOrder;
use App\SalesOrderDetails;
use App\ShoppingCart;
use App\Toko;
use App\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ApiController extends Controller
{
    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        //

        date_default_timezone_set('Asia/Jakarta');
    }

    public function getStatusPresensi(Request $request)
    {
        header('content-type: application/json');

        $user_id = $request->user_id;

        $cek = DB::select("SELECT COUNT(id) AS jml
                           FROM llx_user_presensi
                           WHERE DATE(tgl) = DATE(NOW())
                                 AND user_id = $user_id
                                 AND jenis = 'MASUK'")[0];

        if ($cek->jml > 0) {

            $cek = DB::select("SELECT COUNT(id) AS jml
                               FROM llx_user_presensi
                               WHERE DATE(tgl) = DATE(NOW())
                                    AND user_id = $user_id
                                    AND jenis = 'PULANG'")[0];

            if ($cek->jml > 0) {

                $response["error"]              = false;
                $response["presensi"]["status"] = "MASUK-PULANG";
                $response["error_msg"]          = "Data berhasil diambil";

                echo json_encode($response);
            } else {
                $response["error"]              = false;
                $response["presensi"]["status"] = "PULANG";
                $response["error_msg"]          = "Data berhasil diambil";

                echo json_encode($response);
            }

        } else {
            $response["error"]              = false;
            $response["presensi"]["status"] = "MASUK";
            $response["error_msg"]          = "Data berhasil diambil";

            echo json_encode($response);
        }
    }

    public function postTokenId(Request $request)
    {
        header('content-type: application/json');

        $user_id  = $request->user_id;
        $token_id = $request->token_id;

        DB::table('llx_user')
            ->where('rowid', $user_id)
            ->update(['firebase_token' => $token_id]);

        $response["error"]     = false;
        $response["error_msg"] = "Send Token BERHASIL";

        echo json_encode($response);
    }

    public function postFotoPenyerahan(Request $request)
    {

        $penyerahan_id = $request->pesanan_id;

        $image = $request->foto; // your base64 encoded
        $image = str_replace('data:image/png;base64,', '', $image);
        $image = str_replace(' ', '+', $image);

        $imageName = 'penyerahan-' . $penyerahan_id . '-' . date('Y-m-d') . '.png';
        //$path = public_path() . '/data_file/' . $imageName;
        file_put_contents($imageName, base64_decode($image));

        $so                 = SalesOrder::find($penyerahan_id);
        $so->delivered_foto = $imageName;
        $so->save();

        $response["error"]     = false;
        $response["error_msg"] = "Foto berhasil di uplad";

        echo json_encode($response);

    }

    public function getPengantaran(Request $request)
    {
        header('content-type: application/json');

        //fk_statut => 0:draft, 1:validated,2:in process,3:delivered,
        $pengantaran = DB::select(
            "SELECT a.rowid AS id,
                    b.nom,
                    b.latitude,
                    b.longitude,
                    a.ref,
                    DATE_FORMAT(a.date_commande,'%d/%m/%Y') AS tanggal,
                    IFNULL(a.delivered_foto,'default_delivered.png') AS foto,
                    a.total_ht AS nominal,
                    a.fk_statut AS STATUS
            FROM llx_commande a
            LEFT JOIN llx_societe b ON a.fk_soc = b.rowid
            WHERE a.fk_statut = 2
            ORDER BY date_commande DESC");

        echo json_encode(
            array(
                'error'           => false,
                'message'         => 'Data berhasil diambil',
                'pengantaranList' => $pengantaran,
            )
        );
    }

    public function getPesananToko(Request $request)
    {
        header('content-type: application/json');
        $toko_id = $request->toko_id;

        //fk_statut => 0:draft, 1:validated,2:in process,3:delivered,
        $pesanan = DB::select(
            "SELECT a.rowid AS id,
                        b.nom,
                        b.latitude,
                        b.longitude,
                        a.ref,
                        DATE_FORMAT(a.date_commande,'%d/%m/%Y') AS tanggal,
                        a.total_ht AS nominal,
                        a.fk_statut AS STATUS
            FROM llx_commande a
            LEFT JOIN llx_societe b ON a.fk_soc = b.rowid
            WHERE b.rowid = $toko_id
            ORDER BY date_commande DESC");

        echo json_encode(
            array(
                'error'       => false,
                'message'     => 'Data berhasil diambil',
                'pesananList' => $pesanan,
            )
        );
    }

    public function postPesananSelesai(Request $request)
    {
        date_default_timezone_set('Asia/Jakarta');

        // $image = $request->foto; // your base64 encoded
        // $image = str_replace('data:image/png;base64,', '', $image);
        // $image = str_replace(' ', '+', $image);

        // $imageName = 'penyerahan-' . $request->pesanan_id . '-' . date('Y-m-d') . '.png';
        // //$path = public_path() . '/data_file/' . $imageName;
        // file_put_contents($imageName, base64_decode($image));

        $so            = SalesOrder::find($request->pesanan_id);
        $so->fk_statut = 3;
        // $so->delivered_foto = $imageName;
        $so->date_delivered = date("Y-m-d H:i:s");
        $so->save();

        $so = DB::select(
            "SELECT a.ref,a.delivered_foto AS foto,
                    b.nom AS nama_toko,
                    b.longitude,
                    b.latitude
             FROM llx_commande a
             LEFT JOIN llx_societe b ON a.fk_soc = b.rowid
             WHERE a.rowid = $request->pesanan_id")[0];

        //insert kegiatan
        Kegiatan::create([
            'user_id'    => $request->user_id,
            'tgl'        => date('Y-m-d H:i:s'),
            'latitude'   => $so->latitude,
            'longitude'  => $so->longitude,
            'foto'       => $so->foto,
            'keterangan' => "Penyerahan barang REF: " . $so->ref . " di toko " . $so->nama_toko,
        ]);

        $response["error"]     = false;
        $response["error_msg"] = "Data pesanan berhasil diupdate";

    }

    public function deleteToko(Request $request)
    {
        header('content-type: application/json');

        // //llx_commande
        // $header = DB::select("SELECT rowid FROM llx_commande WHERE fk_soc = $request->toko_id");
        // foreach ($header as $h) {
        //     //$header = DB::select("SELECT rowid FROM llx_commande WHERE fk_soc = $request->toko_id");
        //     SalesOrderDetails::where('fk_commande', $h->rowid)->delete();
        // }
        // //delete pesanan
        // $res = SalesOrder::where('fk_soc', $request->toko_id)->delete();

        // //facture
        // $header_facture = DB::select("SELECT rowid FROM llx_facture WHERE fk_soc = $request->toko_id");
        // //facture detail
        // foreach ($header_facture as $h) {
        //     FactureDetails::where('fk_facture', $h->rowid)->delete();
        //     FactureDetailsRec::where('fk_facture', $h->rowid)->delete();
        // }
        // //facture
        // Facture::where('fk_soc', $request->toko_id)->delete();
        // FactureRec::where('fk_soc', $request->toko_id)->delete();

        // //llx_propal
        // $header_propal = DB::select("SELECT rowid FROM llx_propal WHERE fk_soc = $request->toko_id");
        // foreach ($header_propal as $h) {
        //     //$header = DB::select("SELECT rowid FROM llx_commande WHERE fk_soc = $request->toko_id");
        //     PropalDetails::where('fk_propal', $h->rowid)->delete();
        // }
        // //delete pesanan
        // Propal::where('fk_soc', $request->toko_id)->delete();

        // $so = Toko::find($request->toko_id);
        // $so->delete();

        
        $so = Toko::find($request->toko_id);
        $so->status  = 0;
        
        $so->save();

        $response["error"]     = false;
        $response["error_msg"] = "Data toko berhasil dinonaktifkan";
    }

    public function postPesananBaru(Request $request)
    {

        $row = DB::select("SELECT MAX(rowid) AS id FROM llx_commande")[0];

        $so = SalesOrder::create([
            'ref'                     => $this->generateSOref($row->id + 1),
            'entity'                  => 1,
            'fk_soc'                  => $request->toko_id,
            'tms'                     => new \DateTime(),
            'date_creation'           => new \DateTime(),
            'date_valid'              => new \DateTime(),
            'date_commande'           => new \DateTime(),
            'fk_user_author'          => $request->user_id,
            'fk_user_valid'           => $request->user_id,
            'fk_statut'               => 0,
            'amount_ht'               => 0.00000000,
            'remise_percent'          => 0,
            'remise'                  => 0,
            'total_ht'                => 0,
            'total_ttc'               => 0,
            'facture'                 => 0,
            'fk_cond_reglement'       => 1,
            'fk_mode_reglement'       => 4,
            'date_livraison'          => new \DateTime(),
            'fk_shipping_method'      => 2,
            'fk_availability'         => 1,
            'fk_input_reason'         => 10,
            'fk_incoterms'            => 5,
            'multicurrency_total_ht'  => 0,
            'multicurrency_total_ttc' => 0,

        ]);

        $response["error"]     = false;
        $response["error_msg"] = "Data pesanan berhasil dibuat";

        echo json_encode($response);
    }

    public function deletePesananToko(Request $request)
    {
        header('content-type: application/json');

        $so = SalesOrder::find($request->pesanan_id);
        $so->delete();

        $response["error"]     = false;
        $response["error_msg"] = "Data pesanan berhasil dihapus";

        echo json_encode($response);
    }

    public function postUpdatePesanan(Request $request)
    {
        header('content-type: application/json');

        $sales_order_id = $request->so_id;
        $produk_id      = $request->produk_id;
        $qty            = $request->qty;

        //jika qty == 0 maka hapus detail dan update nilai total

        if ($qty == 0) {
            SalesOrderDetails::where('fk_commande', $sales_order_id)
                ->where('fk_product', $produk_id)
                ->delete();

            $detail        = DB::select("SELECT total_ht FROM llx_commandedet WHERE fk_commande = $sales_order_id");
            $total_nominal = 0;
            foreach ($detail as $d) {
                $total_nominal += $d->total_ht;
            }

            $so            = SalesOrder::find($sales_order_id);
            $so->total_ht  = $total_nominal;
            $so->total_ttc = $total_nominal;

            $so->multicurrency_total_ht  = $total_nominal;
            $so->multicurrency_total_ttc = $total_nominal;

            $so->save();

        } else {
            // cek apakah ada barang ini di salesorderdetail
            $cek = DB::select("SELECT rowid,price
                               FROM llx_commandedet
                               WHERE fk_commande = $sales_order_id
                                     AND fk_product = $produk_id");

            if (count($cek) > 0) {
                //update
                $harga = $cek[0]->price;

                DB::table("llx_commandedet")
                    ->where('fk_commande', '=', $sales_order_id)
                    ->where('fk_product', '=', $produk_id)
                    ->update([
                        'qty'                     => $qty,
                        'total_ht'                => ($harga * $qty),
                        'total_ttc'               => ($harga * $qty),
                        'multicurrency_total_ht'  => ($harga * $qty),
                        'multicurrency_total_ttc' => ($harga * $qty),
                    ]);

            } else {
                //create

                $produk = DB::select("SELECT price AS harga FROM llx_product WHERE rowid = $produk_id")[0];

                SalesOrderDetails::create([
                    'fk_commande'             => $sales_order_id,
                    'fk_product'              => $produk_id,
                    'description'             => "",
                    'qty'                     => $qty,
                    'price'                   => $produk->harga,
                    'subprice'                => ($qty * $produk->harga),
                    'total_ht'                => ($qty * $produk->harga),
                    'total_ttc'               => ($qty * $produk->harga),
                    'multicurrency_total_ht'  => ($qty * $produk->harga),
                    'multicurrency_total_ttc' => ($qty * $produk->harga),
                    'rang'                    => 1,

                ]);
            }

            $detail = DB::select("SELECT total_ht
                                         FROM llx_commandedet
                                         WHERE fk_commande = $sales_order_id");
            $total_nominal = 0;
            foreach ($detail as $d) {
                $total_nominal += $d->total_ht;
            }

            $so            = SalesOrder::find($sales_order_id);
            $so->total_ht  = $total_nominal;
            $so->total_ttc = $total_nominal;

            $so->multicurrency_total_ht  = $total_nominal;
            $so->multicurrency_total_ttc = $total_nominal;

            $so->save();

        }

        $response["error"]     = false;
        $response["error_msg"] = "Update data berhasil";

        echo json_encode($response);

    }

    // public function postPesananToko(Request $request)
    // {

    //     $so = SalesOrder::create([
    //         'ref'                     => $request->ref,
    //         'entity'                  => 1,
    //         'fk_soc'                  => $request->toko_id,
    //         'tms'                     => new \DateTime(),
    //         'date_creation'           => new \DateTime(),
    //         'date_valid'              => new \DateTime(),
    //         'date_commande'           => new \DateTime(),
    //         'fk_user_author'          => $request->user_id,
    //         'fk_user_valid'           => $request->user_id,
    //         'fk_statut'               => 0,
    //         'amount_ht'               => 0.00000000,
    //         'remise_percent'          => 0,
    //         'remise'                  => 0,
    //         'total_ht'                => $request->nilai_pembelian,
    //         'total_ttc'               => $request->nilai_pembelian,
    //         'facture'                 => 0,
    //         'fk_cond_reglement'       => 1,
    //         'fk_mode_reglement'       => 4,
    //         'date_livraison'          => new \DateTime(),
    //         'fk_shipping_method'      => 2,
    //         'fk_availability'         => 1,
    //         'fk_input_reason'         => 10,
    //         'fk_incoterms'            => 5,
    //         'multicurrency_total_ht'  => $request->nilai_pembelian,
    //         'multicurrency_total_ttc' => $request->nilai_pembelian,

    //     ]);

    //     $lastInsertedId = $so->rowid;

    //     $so = DB::select("SELECT * FROM llx_shopping_cart WHERE toko_id = " . $request->toko_id);

    //     foreach ($so as $s) {

    //         SalesOrderDetails::create([
    //             'fk_commande'             => $lastInsertedId,
    //             'fk_product'              => $s->produk_id,
    //             'description'             => "",
    //             'qty'                     => $s->qty,
    //             'price'                   => $s->price,
    //             'subprice'                => ($s->qty * $s->price),
    //             'total_ht'                => ($s->qty * $s->price),
    //             'total_ttc'               => ($s->qty * $s->price),
    //             'multicurrency_total_ht'  => ($s->qty * $s->price),
    //             'multicurrency_total_ttc' => ($s->qty * $s->price),
    //             'rang'                    => 1,

    //         ]);

    //     }

    //     $response["error"]     = false;
    //     $response["error_msg"] = "Data berhasil ditambahkan";

    //     echo json_encode($response);
    //     /*  ==== llx_commande
    //     ref => 'CO2007-0004' (yang depan sama, yang belakang autoinc),
    //     entity =1,
    //     fk_soc = llx_societe (ref ke toko, jadi ini di untuk toko),
    //     tms,date_creation,date_valid, => datetime
    //     date_commande =>date,
    //     fk_user_author => id user yang masukin,
    //     fk_user_valid => id user yang ngevalidasi,
    //     fk_statut => 0:draft, 1:validated,2:in process,3:delivered,
    //     amount_ht = 0.00000000,
    //     remise_percent = 0,
    //     remise = 0,
    //     total_ht, total_ttc => jumlah nilai pembelian (?),
    //     facture = 0,
    //     fk_cond_reglement =1,
    //     fk_mode_reglement = 4,
    //     date_livraison= date now(),
    //     fk_shipping_method = 2,
    //     fk_availability = 1,
    //     fk_input_reason = 10,
    //     fk_incoterms = 5,
    //     multicurrency_total_ht,multicurrency_total_ttc=jumlah nilai pembelian (?),
    //      */

    //     /*== llx_commandedet (detail ?)
    //     fk_commande = id dari llx_commande,
    //     fk_product = id produk,
    //     description = deskripsi,
    //     qty => banyak barang,
    //     price => harga ,
    //     subprice, total_ht,total_ttc,multicurrency_total_ht,multicurrency_total_ttc  => sub total,
    //     rang = 1
    //     */

    // }

    public function postPresensi(Request $request)
    {
        header('content-type: application/json');

        $user_id = $request->user_id;

        // $foto = $request->file('foto');

        $image = $request->foto; // your base64 encoded
        $image = str_replace('data:image/png;base64,', '', $image);
        $image = str_replace(' ', '+', $image);

        $imageName = 'presensi-' . $user_id . '-' . date('Y-m-d') . '.png';
        //$path = public_path() . '/data_file/' . $imageName;
        file_put_contents($imageName, base64_decode($image));

        Presensi::create([
            'user_id'    => $user_id,
            'tgl'        => date('Y-m-d H:i:s'),
            'longitude'  => $request->longitude,
            'latitude'   => $request->latitude,
            'foto'       => $imageName,
            'keterangan' => $request->keterangan,
            'jenis'      => $request->status,
        ]);

        $response["error"]     = false;
        $response["error_msg"] = "Presensi berhasil";

        echo json_encode($response);

    }

    public function postKegiatan(Request $request)
    {
        header('content-type: application/json');

        $user_id = $request->user_id;

        $foto = $request->file('foto');

        $image = $request->foto; // your base64 encoded
        $image = str_replace('data:image/png;base64,', '', $image);
        $image = str_replace(' ', '+', $image);

        $imageName = 'kegiatan-' . $user_id . '-' . date('Y-m-d') . '.png';
        //$path = public_path() . '/data_file/' . $imageName;
        file_put_contents($imageName, base64_decode($image));

        Kegiatan::create([
            'user_id'    => $user_id,
            'tgl'        => new \DateTime(),
            'longitude'  => $request->longitude,
            'latitude'   => $request->latitude,
            'foto'       => $imageName,
            'keterangan' => $request->keterangan,
        ]);

        $response["error"]     = false;
        $response["error_msg"] = "Kegiatan berhasil";

        echo json_encode($response);

    }

    public function getKeranjangBelanjaList(Request $request)
    {
        header('content-type: application/json');

        $toko_id   = $request->toko_id;
        $keranjang = DB::select("SELECT id,toko_id,product_id,qty,price
                                 FROM llx_shopping_cart
                                 WHERE toko_id = $toko_id");

        echo json_encode(
            array(
                'error'         => false,
                'message'       => 'Data berhasil diambil',
                'keranjangList' => $keranjang,
            )
        );
    }

    /**
     *
     */
    public function postKeranjangBelanja(Request $request)
    {
        ShoppingCart::create([
            'toko_id'    => $request->toko_id,
            'product_id' => $request->product_id,
            'qty'        => $request->qty,
            'price'      => $request->price,
        ]);

        $response["error"]     = false;
        $response["error_msg"] = "Barang berhasil ditambahkan";

        echo json_encode($response);
    }

    public function getFile(Request $request)
    {
        $imageFileName = $request->imagefile;
        $path          = 'C:\\dolibarr\\dolibarr_documents\\produit\\';

        $fullpath = $path . $imageFileName;

        if (!is_file($fullpath)) {
            die('Image doesnt exist');
        } else {
            $size    = filesize($fullpath);
            $content = file_get_contents($fullpath);
            header("Content-type: image/jpeg");
            echo $content;
        }
    }

    public function getProdukList(Request $request)
    {
        header('content-type: application/json');

        $so_id  = $request->so_id;
        $filter = $request->filter;

        // $sodet = SalesOrderDetails::where([
        //     ['fk_product', '=', $p->id],
        //     ['fk_commande', '=', $so_id],
        // ])->get();

        // $included_me = "";
        // foreach($sodet as $s){
        //     $included_me .= $s->id .",";
        // }

        // $included_me = substr($included_me,0,-1);

        $produk = DB::select(
            "SELECT DISTINCT p.rowid AS id,
                            -- CONCAT('document.php?modulepart=produit&attachment=0&file=',p.ref,'/',ecmfile.filename,  '&entity=1') AS foto,
                            CONCAT(p.ref,'/',ecmfile.filename) AS foto,
                            p.ref AS ref,
                            p.label AS nama,
                            p.description AS deskripsi,
                            ROUND(p.price,0) AS harga -- ,
                            -- p.fk_product_type AS p_fk_product_type,
                            -- p.tosell AS p_tosell,
                            -- p.tobuy AS p_tobuy,
                            -- p.url AS p_url,
                            -- p.customcode AS p_customcode,
                            -- p.fk_country AS p_fk_country,
                            -- p.accountancy_code_sell AS p_accountancy_code_sell,
                            -- p.accountancy_code_sell_intra AS p_accountancy_code_sell_intra,
                            -- p.accountancy_code_sell_export AS p_accountancy_code_sell_export,
                            -- p.accountancy_code_buy AS p_accountancy_code_buy,
                            -- p.note AS p_note,
                            -- p.note_public AS p_note_public,
                            -- p.weight AS p_weight,
                            -- p.weight_units AS p_weight_units,
                            -- p.length AS p_length,
                            -- p.length_units AS p_length_units,
                            -- p.width AS p_width, p.width_units AS p_width_units,
                            -- p.height AS p_height,
                            -- p.height_units AS p_height_units,
                            -- p.surface AS p_surface,
                            -- p.surface_units AS p_surface_units,
                            -- p.volume AS p_volume,
                            -- p.volume_units AS p_volume_units,
                            -- p.duration AS p_duration,
                            -- p.finished AS p_finished,
                            -- p.price_base_type AS p_price_base_type,
                            -- p.price AS p_price,
                            -- p.price_ttc AS p_price_ttc,
                            -- p.price_min,
                            -- p.price_min_ttc,
                            -- p.tva_tx AS p_tva_tx, p.datec AS p_datec,
                            -- p.tms AS p_tms, p.cost_price AS p_cost_price,
                            -- p.stock AS p_stock,
                            -- p.seuil_stock_alerte AS p_seuil_stock_alerte,
                            -- p.desiredstock AS p_desiredstock,
                            -- p.pmp AS p_pmp, s.nom AS s_nom,
                            -- pf.ref_fourn AS pf_ref_fourn,
                            -- pf.quantity AS pf_quantity,
                            -- pf.remise_percent AS pf_remise_percent,
                            -- pf.unitprice AS pf_unitprice,
                            -- pf.delivery_time_days AS pf_delivery_time_days,
                            -- ecmfile.filename AS product_image
                FROM llx_product AS p
                -- LEFT JOIN llx_product_extrafields AS extra ON p.rowid = extra.fk_object
                -- LEFT JOIN llx_product_fournisseur_price AS pf ON pf.fk_product = p.rowid
                -- LEFT JOIN llx_societe s ON s.rowid = pf.fk_soc
                LEFT JOIN llx_ecm_files AS ecmfile ON CONCAT('produit/',p.ref) = ecmfile.filepath
                WHERE ((p.label LIKE '%$filter%') OR (p.description LIKE '%$filter%'))
                      AND p.fk_product_type = 0
                      AND p.entity IN (1)
                      AND p.tosell=1 AND p.tobuy=1
                GROUP BY p.rowid");

        $product_list = array();

        if (count($produk) > 0) {
            foreach ($produk as $p) {
                //DB::select("");
                $sodet = SalesOrderDetails::where([
                    ['fk_product', '=', $p->id],
                    ['fk_commande', '=', $so_id],
                ])->get();

                // if ($filter === "") {
                    

                //     if (count($sodet) > 0) {
                //         $so = $sodet->first();

                //         $arr['id']        = $p->id;
                //         $arr['ref']       = $p->ref;
                //         $arr['foto']      = $p->foto;
                //         $arr['nama']      = $p->nama;
                //         $arr['deskripsi'] = $p->deskripsi;
                //         $arr['harga']     = $so->price;
                //         $arr['qty']       = $so->qty;

                //         array_push($product_list, $arr);

                //     }

                   
                // } else {

                    if (count($sodet) > 0) {
                        $so = $sodet->first();

                        $arr['id']        = $p->id;
                        $arr['ref']       = $p->ref;
                        $arr['foto']      = $p->foto;
                        $arr['nama']      = $p->nama;
                        $arr['deskripsi'] = $p->deskripsi;
                        $arr['harga']     = $so->price;
                        $arr['qty']       = $so->qty;

                        array_push($product_list, $arr);

                    } else {

                        $arr['id']        = $p->id;
                        $arr['ref']       = $p->ref;
                        $arr['foto']      = $p->foto;
                        $arr['nama']      = $p->nama;
                        $arr['deskripsi'] = $p->deskripsi;
                        $arr['harga']     = $p->harga;
                        $arr['qty']       = 0;

                        array_push($product_list, $arr);

                    }

                   
                // }

            }

            echo json_encode(
                array(
                    'error'       => false,
                    'message'     => 'Data berhasil diambil',
                    'productList' => $product_list,
                )
            );

        } else {

            echo json_encode(
                array(
                    'error'       => false,
                    'message'     => 'Data berhasil diambil',
                    'productList' => $product_list,
                )
            );
        }

       

    }

    public function getKegiatanDetailList(Request $request)
    {
        header('content-type: application/json');

        $user_id = $request->user_id;
        $tanggal = $request->tanggal;

        $kegiatan = DB::select("SELECT id,
                                       CONCAT('Jam: ',TIME(tgl)) AS tgl,
                                       latitude,
                                       longitude,
                                       foto,
                                       IFNULL(keterangan,'-') AS keterangan
                                FROM llx_user_activity
                                WHERE user_id = $user_id AND DATE(tgl) = '$tanggal'
                                ORDER BY tgl DESC");

        echo json_encode(
            array(
                'error'              => false,
                'message'            => 'Data berhasil diambil',
                'kegiatanDetailList' => $kegiatan,
            )
        );
    }

    public function getKegiatanList(Request $request)
    {
        header('content-type: application/json');

        $user_id = $request->user_id;

        $kegiatan = DB::select("SELECT DATE(tgl) AS tgl,
                                       COUNT(id) AS jml_kunjungan
                                FROM llx_user_activity
                                WHERE user_id = $user_id
                                GROUP BY DATE(tgl)
                                ORDER BY DATE(tgl) DESC                                
                                LIMIT 7");

        echo json_encode(
            array(
                'error'        => false,
                'message'      => 'Data berhasil diambil',
                'kegiatanList' => $kegiatan,
            )
        );
    }

    public function getPresensiList(Request $request)
    {
        date_default_timezone_set('Asia/Jakarta');

        header('content-type: application/json');

        $user_id = $request->user_id;

        $kegiatan = DB::select(
            "SELECT a.fulldate AS tanggal,
                    IFNULL(b.masuk,'BELUM !') AS jam_masuk,
                    IFNULL(c.pulang,'BELUM !') AS jam_pulang
            FROM llx_dates a
            LEFT JOIN (SELECT user_id,
                            DATE(tgl) AS tgl,
                            TIME(tgl) AS masuk
                        FROM llx_user_presensi
                        WHERE user_id = $user_id
                                AND jenis = 'MASUK') b ON a.fulldate = b.tgl
            LEFT JOIN (SELECT user_id,
                            DATE(tgl) AS tgl,
                            TIME(tgl) AS pulang
                        FROM llx_user_presensi
                        WHERE user_id = $user_id
                                AND jenis = 'PULANG') c ON a.fulldate = c.tgl
            -- WHERE DATE(NOW() + INTERVAL 1 DAY) >= DATE(a.fulldate)
            WHERE DATE(NOW()) >= DATE(a.fulldate)
            ORDER BY a.fulldate DESC
            LIMIT 7");

        echo json_encode(
            array(
                'error'        => false,
                'message'      => 'Data berhasil diambil',
                'presensiList' => $kegiatan,
            )
        );
    }

    /**
     * * yang diperlukan id,nama,alamat,longitude,latitude,foto_toko
     * FIXME
     */
    public function getTokoList(Request $request)
    {
        header('content-type: application/json');

        $filter = $request->filter;

        $toko = DB::select(
            "SELECT s.rowid AS id,
                    s.nom AS nama,
                    s.address AS alamat,
                    s.town AS kota,
                    s.longitude,
                    s.latitude  ,
                    IFNULL(s.logo,'default_toko.png') AS foto
                    -- s.name_alias AS alias,
                    -- s.status AS s_status,
                    -- s.client AS s_client,
                    -- s.code_client AS s_code_client,

                    -- d.nom AS d_nom
            FROM llx_societe AS s
            LEFT JOIN llx_c_departements AS d ON s.fk_departement = d.rowid
            WHERE  ((s.nom LIKE '%$filter%') OR (s.address LIKE '%$filter%'))
                   AND s.entity IN (1)
                   AND s.status = 1
                   AND s.fournisseur NOT IN (1)"
        );

        echo json_encode(
            array(
                'error'    => false,
                'message'  => 'Data berhasil diambil',
                'tokoList' => $toko,
            )
        );
    }

    public function getTokoProfile(Request $request)
    {

        header('content-type: application/json');

        $toko_id = $request->toko_id;

        $toko = DB::select("SELECT rowid,nom,address,town,longitude,latitude,IFNULL(logo,'default_toko.png') AS logo
                            FROM llx_societe
                            WHERE rowid= $toko_id")[0];

        //echo json_encode($toko);
        $response['error']             = false;
        $response['toko']['rowid']     = $toko->rowid;
        $response['toko']['nama']      = $toko->nom;
        $response['toko']['alamat']    = $toko->address;
        $response['toko']['kota']      = $toko->town;
        $response['toko']['foto']      = $toko->logo;
        $response['toko']['longitude'] = $toko->longitude;
        $response['toko']['latitude']  = $toko->latitude;
        echo json_encode($response);
    }

    /**
     * update data toko atau buat data toko baru
     * jika $toko_id == 0 , buat data baru
     */
    public function postTokoProfile(Request $request)
    {
        header('content-type: application/json');

        $toko_id = $request->toko_id;

        if ($toko_id != 0) {

            // $foto = $request->file('foto');

            $image = $request->foto; // your base64 encoded
            $image = str_replace('data:image/png;base64,', '', $image);
            $image = str_replace(' ', '+', $image);

            $imageName = 'toko-' . $toko_id . '.png';
            //$path = public_path() . '/data_file/' . $imageName;
            file_put_contents($imageName, base64_decode($image));

            //update
            $toko            = Toko::find($toko_id);
            $toko->nom       = $request->nama;
            $toko->address   = $request->alamat;
            $toko->logo      = $imageName;
            $toko->town      = $request->kota;
            $toko->longitude = $request->longitude;
            $toko->latitude  = $request->latitude;

            $toko->save();

            $response["error"]     = false;
            $response["error_msg"] = "Data berhasil diubah";

            echo json_encode($response);
        } else {
            //insert
            $row = DB::select("SELECT MAX(rowid) AS id FROM llx_societe")[0];

            $image = $request->foto; // your base64 encoded
            $image = str_replace('data:image/png;base64,', '', $image);
            $image = str_replace(' ', '+', $image);

            $imageName = 'toko-' . ($row->id + 1) . '.png';
            //$path = public_path() . '/data_file/' . $imageName;
            file_put_contents($imageName, base64_decode($image));

            Toko::create([
                'nom'            => $request->nama,
                'address'        => $request->alamat,
                'town'           => $request->kota,
                'longitude'      => $request->longitude,
                'latitude'       => $request->latitude,
                'logo'           => $imageName,
                'entity'         => 1,
                'fournisseur'    => 0,
                'code_client'    => $this->generateCodeClient($row->id + 1),
                'fk_departement' => 1032, //jawa timur
            ]);

            $response["error"]     = false;
            $response["error_msg"] = "Data berhasil ditambahkan";

            echo json_encode($response);
        }

    }

    private function generateSOref($kode)
    {
        return 'CU2007-' . str_pad($kode, 4, "0", STR_PAD_LEFT);
    }

    private function generateCodeClient($kode)
    {
        return 'CU2020-' . str_pad($kode, 5, "0", STR_PAD_LEFT);
    }

    /**
     *
     *
     */
    public function postLogin(Request $request)
    {

        header('content-type: application/json');

        $username = $request->username;
        $password = $request->password;

        $user = User::where('login', $username);

        if ($user->count() > 0) {

            $password_hash = $user->first()->pass_crypted;

            if (password_verify($password, $password_hash)) {

                $user_id = $user->first()->rowid;

                $llx_usergroup = DB::select("SELECT a.fk_usergroup,b.nom AS level
                                             FROM llx_usergroup_user a
                                             LEFT JOIN llx_usergroup b ON a.fk_usergroup = b.rowid
                                             WHERE a.fk_user = $user_id")[0];

                $response["error"]                = false;
                $response["user"]["id"]           = $user_id;
                $response["user"]["username"]     = $user->first()->login;
                $response["user"]["nama_lengkap"] = $user->first()->firstname . '-' . $user->first()->lastname;
                $response["user"]["email"]        = $user->first()->email;
                $response["user"]["level"]        = $llx_usergroup->level;

                echo json_encode($response);
            } else {
                $response["error"]     = true;
                $response["error_msg"] = "Periksa kembali username dan password anda";
                echo json_encode($response);
            }
        } else {
            $response["error"]     = true;
            $response["error_msg"] = "Periksa kembali username dan password anda";
            echo json_encode($response);
        }
    }

    //
}
