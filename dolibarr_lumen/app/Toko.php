<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Toko extends Model
{
    protected $table = 'llx_societe';
    protected $primaryKey = 'rowid';
    protected $fillable = [
        'nom','name_alias','entity','fournisseur','code_client',
        'address','town','fk_departement','longitude','latitude',
        'logo','status'
    ];
}
