<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Presensi extends Model
{
    protected $table = 'llx_user_presensi';
    protected $fillable = [
        'user_id','tgl','latitude','longitude','foto','keterangan','jenis'
    ];
}
