<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class ShoppingCart extends Model
{
    protected $table = 'llx_shopping_cart';
    /***/
    protected $fillable = ['toko_id','product_id','qty','price'];
}
