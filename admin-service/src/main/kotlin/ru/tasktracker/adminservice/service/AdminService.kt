package ru.tasktracker.adminservice.service

import cz.encircled.skom.Extensions.mapTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.tasktracker.adminservice.dto.RoleDto
import ru.tasktracker.adminservice.exception.RoleException
import ru.tasktracker.adminservice.repository.RoleRepository


interface AdminService {
    fun addRole(roleDto: RoleDto): List<RoleDto>
}

@Service
class AdminServiceImpl(
    val roleRepository: RoleRepository
): AdminService {

    @Transactional
    override fun addRole(roleDto: RoleDto): List<RoleDto> {
        if (roleRepository.existsRoleByRole(roleDto.role))
            throw RoleException("Role ${roleDto.role} already exists")

        roleRepository.save(roleDto.mapTo())
        return roleRepository.findAll().mapTo()
    }

}