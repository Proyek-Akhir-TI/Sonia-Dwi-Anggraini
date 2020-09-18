<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Kegiatan extends Model
{
    protected $table = 'llx_user_activity';
    protected $fillable = [
        'user_id','tgl','latitude','longitude','foto','keterangan'
    ];
}
