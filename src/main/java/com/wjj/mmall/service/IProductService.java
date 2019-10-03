package com.wjj.mmall.service;

import com.github.pagehelper.PageInfo;
import com.wjj.mmall.common.ServerResponse;
import com.wjj.mmall.pojo.Product;
import com.wjj.mmall.vo.ProductDetailVo;

public interface IProductService {
    ServerResponse saveeOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
