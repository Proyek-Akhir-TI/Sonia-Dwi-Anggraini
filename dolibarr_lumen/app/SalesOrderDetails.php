<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class SalesOrderDetails extends Model
{
    protected $table = 'llx_commandedet';
    /***/
    protected $fillable = [
        'fk_commande',
        'fk_product',
        'description',
        'qty',
        'price',
        'subprice',
        'total_ht',
        'total_ttc',
        'multicurrency_total_ht',
        'multicurrency_total_ttc',
        'rang',
    ];
}
