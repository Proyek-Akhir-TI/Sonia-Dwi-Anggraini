<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class SalesOrder extends Model
{

    protected $table = 'llx_commande';
    protected $primaryKey = 'rowid';
    protected $fillable = [
        'ref',
        'entity',
        'fk_soc',
        'tms',
        'date_creation',
        'date_valid',
        'date_commande',
        'fk_user_author',
        'fk_user_valid',
        'fk_statut',
        'date_delivered',
        'amount_ht',
        'remise_percent',
        'remise',
        'total_ht',
        'total_ttc',
        'facture',
        'fk_cond_reglement',
        'fk_mode_reglement',
        'date_livraison',
        'fk_shipping_method',
        'fk_availability',
        'fk_input_reason',
        'fk_incoterms',
        'multicurrency_total_ht',
        'multicurrency_total_ttc',
        'delivered_foto'
    ];
}
