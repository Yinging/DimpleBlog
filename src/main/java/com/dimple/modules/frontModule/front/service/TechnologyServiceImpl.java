package com.dimple.modules.frontModule.front.service;

import com.dimple.modules.endModule.blogManager.repository.BlogRepository;
import com.dimple.modules.frontModule.front.domain.BlogDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @className: TechnologyServiceImpl
 * @description:
 * @auther: Owenb
 * @date: 01/25/19
 * @version: 1.0
 */
@Service
@Transactional
public class TechnologyServiceImpl implements TechnologyService {

    @Autowired
    BlogRepository blogRepository;

    @Override
    public Page<BlogDomain> getNewestBlog(Pageable pageable) {

        Page<BlogDomain> allBlogVo = blogRepository.getAllBlogVo(pageable);
        return allBlogVo;
    }
}
