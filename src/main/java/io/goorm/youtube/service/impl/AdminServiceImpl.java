package io.goorm.youtube.service.impl;


import io.goorm.youtube.repository.AdminRepository;
import io.goorm.youtube.domain.Admin;
import io.goorm.youtube.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {


    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {

        this.adminRepository = adminRepository;
    }

    public Admin login(Admin admin) {

        return adminRepository.findByAdminId(admin.getAdminId());
    }

    public List<Admin> findAll() {

        return adminRepository.findAll();

    }

    public Admin find(Long adminSeq) throws Exception {

        var result = adminRepository.findById(adminSeq).orElseThrow(()-> new Exception("일치하는 회원이 없습니다."));
        return result;
    }

    public Admin save(Admin admin) {

        return adminRepository.save(admin);
    }

    public Admin update(Long adminSeq, Admin admin) {

        Admin existingAdmin = adminRepository.findById(adminSeq)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        BeanUtils.copyProperties(admin, existingAdmin, "adminSeq","regName", "regDate");

        return adminRepository.save(existingAdmin);

    }

    public Admin remove(Admin admin) {

        Admin existingAdmin = adminRepository.findById(admin.getAdminSeq()).orElseThrow();

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin updateUseYn(Long adminSeq) {

        Admin existingAdmin = adminRepository.findById(adminSeq).orElseThrow(() -> new RuntimeException("Admin not found"));


        if (existingAdmin != null && existingAdmin.getUseYn().equals("N")) {
            existingAdmin.setUseYn("Y");
        } else {
            existingAdmin.setUseYn("N");
        }

        return adminRepository.save(existingAdmin);
    }


}
