package com.example.myapplication.Fagment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Adapter.LoaiNhaHangAdapter;
import com.example.myapplication.Adapter.NhaHangAdapter;
import com.example.myapplication.Model.DanhGiaNH;
import com.example.myapplication.Model.LoaiNhaHang;
import com.example.myapplication.Model.NhaHang;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class NhaHangFragment extends Fragment {
    private List<NhaHang> listNhaHang;
    private List<DanhGiaNH> listDanhGia;
    private List<LoaiNhaHang> listLoaiNhaHang;
    private NhaHang nhaHang;

    private RecyclerView rcv_nhahang;
    private RecyclerView rcv_loainhahang;

    //Firestore
    FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        //Lấy danh sách đánh giá xuống
        getAllDanhGia(getContext());

        //Lấy danh sách loại nhà hàng xuống
        getAllLoaiNhaHang(getContext());

        getAllNhaHang(getContext());

        Toast.makeText(getContext(), listNhaHang.size()+" "+listLoaiNhaHang.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nha_hang, container, false);
        rcv_nhahang =v.findViewById(R.id.rcv_restaurant);
        rcv_loainhahang =v.findViewById(R.id.rcv_categoryRes);


        return v;
    }


    //Xuất tất cả nhà nhà hàng lên list
    public void getAllNhaHang(Context context){
        listNhaHang = new ArrayList<>();

        final CollectionReference reference = db.collection("NHAHANG");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaNH = doc.get("MaNH").toString();
                            String MaLoaiNH = doc.get("MaLoaiNH").toString();
                            String MaTK = doc.get("MaTK").toString();
                            String TenNH = doc.get("TenNH").toString();
                            String ThoiGian = doc.get("ThoiGian").toString();
                            int PhiVanChuyen = Integer.parseInt(doc.get("PhiVanChuyen").toString());
                            String HinhAnh = doc.get("HinhAnh").toString();

                            nhaHang = new NhaHang(MaNH, getTenLoaiNhaHang(MaLoaiNH), MaTK, TenNH, ThoiGian, PhiVanChuyen, HinhAnh, tinhDanhGiaTB(MaNH));
                            listNhaHang.add(nhaHang);

                        }
                        NhaHangAdapter adapter  = new NhaHangAdapter(listNhaHang, getContext());
                        rcv_nhahang.setLayoutManager(new LinearLayoutManager(getContext()));
                        rcv_nhahang.setAdapter(adapter);
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Tính lượt sao của nhà hàng
    private Double tinhDanhGiaTB(String maNH){
        for (DanhGiaNH dg: listDanhGia){
            if(maNH.equals(dg.getMaNH()))
                return (Double.valueOf(dg.getTongDG() / dg.getLuotDG()));
        }
        return 0.0;
    }

    //Lấy tên loại của nhà hàng
    private String getTenLoaiNhaHang(String maLoaiNH){
        for (LoaiNhaHang loai: listLoaiNhaHang){
            if(maLoaiNH.equals(loai.getMaLoaiNH()))
                return loai.getTenLoaiNH();
        }
        return "";
    }



    //Xuất tất cả đánh giá lên list
    public void getAllDanhGia(Context context){
        listDanhGia = new ArrayList<>();

        final CollectionReference reference = db.collection("DANHGIANH");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaDanhGia = doc.get("MaDanhGia").toString();
                            String MaNH = doc.get("MaNH").toString();
                            int LuotDG = Integer.parseInt(doc.get("LuotDG").toString());
                            int TongDG = Integer.parseInt(doc.get("TongDG").toString());

                            DanhGiaNH danhGiaNH = new DanhGiaNH(MaDanhGia, MaNH, LuotDG, TongDG);
                            listDanhGia.add(danhGiaNH);
                        }

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Xuất tất cả đánh giá lên list
    public void getAllLoaiNhaHang(Context context){
        listLoaiNhaHang = new ArrayList<>();

        final CollectionReference reference = db.collection("LOAINHAHANG");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaLoaiNH = doc.get("MaLoaiNH").toString();
                            String TenLoaiNH = doc.get("TenLoaiNH").toString();
                            String HinhAnh = doc.get("HinhAnh").toString();

                            LoaiNhaHang loaiNhaHang =  new LoaiNhaHang(MaLoaiNH, TenLoaiNH, HinhAnh);
                            listLoaiNhaHang.add(loaiNhaHang);
                        }

                        LinearLayoutManager layoutManager
                                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

                        LoaiNhaHangAdapter adapter  = new LoaiNhaHangAdapter(listLoaiNhaHang, getContext());
                        rcv_loainhahang.setLayoutManager(layoutManager);
                        rcv_loainhahang.setAdapter(adapter);

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}