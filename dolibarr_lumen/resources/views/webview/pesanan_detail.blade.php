@extends('webview.layout')
@section('content')
<table class="table">
    <thead>
      <tr>
        <th>Produk</th>  
        <th>Qty</th>          
        <th>Harga</th>   
        <th>Sub Total</th>      
      </tr>
    </thead>
    <tbody>
      @php 
        $total = 0;
      @endphp  
      @foreach($pesananList as $j)
      <tr>
        <td>{{ $j->nama_produk }}</td>
        <td>{{ $j->qty }} </td>        
        <td>{{ $j->harga }} </td>        
        <td>{{ $j->subtotal }} </td>        
        @php 
            $total += $j->subtotal;
        @endphp    
      </tr>      
      @endforeach
      <tr class="table-warning">
        <td colspan="3">TOTAL</td>
        <td>
            @php 
            echo $total;
            @endphp
        </td> 
      </tr>
    </tbody>
  </table>
@endsection