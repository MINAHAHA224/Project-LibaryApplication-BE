package com.ThuVien.ThuVienAplication.storeprocedure.bookTitleSp;

public class QueryStoreProcedure {
        public static final String SP_Tong_So_Sach  = "EXECUTE SP_Tong_So_Sach";

        public static final String SP_Tong_Doc_Gia  = "EXECUTE SP_Tong_Doc_Gia";

        public static final String SP_Tong_Sach_Muon  = "EXECUTE SP_Tong_Sach_Muon ";


        public static final String SP_Danh_Sach_Dau_Sach  = "EXECUTE SP_Danh_Sach_Dau_Sach ";


        public static final String SP_ThemDauSach   = "EXECUTE sp_ThemDauSach ?,?,?,?,?,?,?,?,?,?,?,?,? ";

        public static final String sp_UpdateDauSach   = "EXECUTE sp_UpdateDauSach ?,?,?,?,?,?,?,?,?,?,?,?,?,? ";
        public static final String SP_CapNhatDauSach   = "EXECUTE SP_CapNhatDauSach ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ";

        public static final String SP_Xoa_Dau_Sach  = "EXECUTE SP_Xoa_Dau_Sach ? ";



        public static final String SP_XOA_TACGIA_SACH  = "EXECUTE SP_XOA_TACGIA_SACH ?,?";

        public static final String Sp_ThemSach  = "EXECUTE Sp_ThemSach ?, ? , ? , ? , ? , ?";

        public static final String Sp_CapNhatSach  = "EXECUTE Sp_CapNhatSach ? , ?, ? , ? , ? , ?";

        public static final String Sp_Check_Sch_CT_PHIEUMUON  = "EXECUTE Sp_Check_Sch_CT_PHIEUMUON  ? ";
        public static final String Sp_XoaSach  = "EXECUTE Sp_XoaSach ? , ? ";
}
