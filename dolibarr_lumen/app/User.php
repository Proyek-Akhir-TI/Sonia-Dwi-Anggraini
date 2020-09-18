<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    protected $table = 'llx_user';
    protected $fillable = [
        'firebase_token'
    ];
}
