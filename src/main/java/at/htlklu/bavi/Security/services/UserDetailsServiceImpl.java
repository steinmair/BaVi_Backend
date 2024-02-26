package at.htlklu.bavi.Security.services;

import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    MembersRepository membersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = membersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not Found with Email: " + email));

        return UserDetailsImpl.build(member);
    }

}
