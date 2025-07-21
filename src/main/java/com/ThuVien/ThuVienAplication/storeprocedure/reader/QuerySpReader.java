package com.ThuVien.ThuVienAplication.storeprocedure.reader;

public class QuerySpReader {
    public static final String SP_Thong_ke_doc_gia = "EXECUTE SP_Thong_ke_doc_gia ";
    public static final String SP_Tao_moi_doc_gia = "EXECUTE SP_Tao_moi_doc_gia ?,?,?,?,?,?,?,?,?,?,? ";
    public static final String SP_Tao_moi_doc_gia_undo = "EXECUTE SP_Tao_moi_doc_gia_undo ?,?,?,?,?,?,?,?,?,?,?,? ";
    public static final String SP_Tim_doc_gia = "EXECUTE SP_Tim_doc_gia ? ";

    public static final String SP_Xoa_doc_gia = "EXECUTE SP_Xoa_doc_gia ? ";
    public static final String SP_Cap_nhat_doc_gia = "EXECUTE SP_Cap_nhat_doc_gia ?,?,?,?,?,?,?,?,?,?,?,? ";
}
