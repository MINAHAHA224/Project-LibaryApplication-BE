package com.ThuVien.ThuVienAplication.storeprocedure.bookTitleSp;

public class QueryGetData {

    public static final String SP_Data_The_loai = "EXECUTE SP_Data_The_loai";

    public static final String  SP_Data_Ngon_Ngu = "EXECUTE SP_Data_Ngon_Ngu";

    public static final String  SP_Tim_Tac_Gia = "EXECUTE SP_Tim_Tac_Gia ? ";

    public static final String  SP_Tim_Ngon_Ngu = "EXECUTE SP_Tim_Ngon_Ngu ? ";

    public static final String  SP_Tim_The_Loai = "EXECUTE SP_Tim_The_Loai ? ";

    public static final String SP_Danh_Tac_Gia_Cho_DauSachCreate  = "EXECUTE SP_Danh_Tac_Gia_Cho_DauSachCreate ";

    public static final String SP_Danh_Tac_Gia_Test  = "EXECUTE SP_Danh_Tac_Gia_Test ";
    public static final String SP_Thong_tin_Dau_Sach  = "EXECUTE SP_Thong_tin_Dau_Sach ?";
    public static final String SP_Check_Sach_DauSach  = "EXECUTE SP_Check_Sach_DauSach ?";

    public static final String Check_Thong_tin_Dau_Sach  = "EXECUTE Check_Thong_tin_Dau_Sach ?";
    public static final String SP_Thong_tin_Dau_Sach_Test  = "EXECUTE SP_Thong_tin_Dau_Sach_Test";


    public static final String SP_Thong_tin_Dau_Sach_Test_cho_excel  = "EXECUTE SP_Thong_tin_Dau_Sach_Test_cho_excel";

    public static final String SP_Thong_tin_Ngan_Tu  = "EXECUTE SP_Thong_tin_Ngan_Tu";

    public static final String SP_Thong_tin_Ngan_Tu_Detail  = "EXECUTE SP_Thong_tin_Ngan_Tu_Detail ? ";
    public static final String SP_Thong_tin_Sach_DauSach  = "EXECUTE SP_Thong_tin_Sach_DauSach ? ";

    public static final String SP_Check_Thong_tin_Sach_DauSach  = "EXECUTE SP_Check_Thong_tin_Sach_DauSach ? , ? ";

    // === BỔ SUNG SP MỚI ===
    public static final String SP_GetBookById = "{call SP_GetBookById(?, ?)}";
}
