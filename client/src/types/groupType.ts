export interface Group {
  id: string;
  title: string;
  image: string;
  members: string;
  memberAvatars: string[];
  moreMembers: number;
  category: string;
}

export interface GroupItem {
  id: number;
  maNguoiTao: number;
  tenNhom: string;
  moTa: string;
  anhBia: string;
  anhDaiDien: string;
  soThanhVien: number;
  loaiNhom: string;
  canDuyetDangBai: boolean;
  ngayTao: string;
}
