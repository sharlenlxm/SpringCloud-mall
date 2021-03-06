package com.mall.admin.spike.controller;

import com.mall.admin.spike.service.SpikeService;
import com.mall.admin.spike.util.ConnectionUtil;
import com.mall.common.pojo.*;
import com.mall.common.util.DateUtil;
import com.mall.common.util.JWTUtil;
import com.mall.common.vo.ResultVO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * SpikeController
 *
 * @Author BessCroft
 * @Date 2020/9/27 10:22
 */
@RestController
@CrossOrigin
@RequestMapping("/spike")
@Api(tags = "秒杀服务接口")
public class SpikeController {

    @Resource
    private SpikeService spikeService;

    @Resource
    private RedissonClient redissonClient;

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    @ApiOperation(value = "秒杀服务添加接口" , notes = "添加秒杀的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "秒杀标题", required = true, type = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", required = true, type = "String"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", required = true, type = "String"),
            @ApiImplicitParam(name = "status", value = "上下线状态", required = true, type = "Integer"),
            @ApiImplicitParam(name = "productId", value = "关联的商品id", required = true, type = "String"),
            @ApiImplicitParam(name = "flashPromotionPrice", value = "限时购价格", required = true, type = "String"),
            @ApiImplicitParam(name = "flashPromotionCount", value = "限时购数量", required = true, type = "String"),
            @ApiImplicitParam(name = "flashPromotionLimit", value = "每人限购数量", required = true, type = "String"),
            @ApiImplicitParam(name = "token", value = "token验证信息", required = true, type = "String")
    })
    public ResultVO insertSpike(@RequestParam String title,
                                @RequestParam String startDate,
                                @RequestParam String endDate,
                                @RequestParam Integer status,
                                @RequestParam String productId,
                                @RequestParam double flashPromotionPrice,
                                @RequestParam Integer flashPromotionCount,
                                @RequestParam Integer flashPromotionLimit,
                                @RequestHeader(required = true) String token) {
        Jws<Claims> jws = JWTUtil.Decrypt(token);
        String adminId = jws.getBody().getId();
        String issuer = jws.getBody().getIssuer();
        if ("admin".equals(issuer)) {
            System.out.println(startDate);
            System.out.println(endDate);
            Date startdate = DateUtil.JSToJava(startDate);
            Date enddate = DateUtil.JSToJava(endDate);
            FlashPromotion flashPromotion = new FlashPromotion(0,title,startdate,enddate,status,new Date());
            FlashPromotionProductRelation flashPromotionProductRelation = new FlashPromotionProductRelation(0,0,0,productId,flashPromotionPrice,flashPromotionCount,flashPromotionLimit,1);
            boolean b = spikeService.insertSpike(flashPromotion, flashPromotionProductRelation);
            if (b) {
                return new ResultVO(0,"success");
            } else {
                return new ResultVO(1,"fail");
            }
        } else {
            return new ResultVO(1,"没有权限，请联系管理员",null);
        }
    }

    @RequestMapping(value = "/product",method = RequestMethod.GET)
    @ApiOperation(value = "秒杀服务商品查询接口" , notes = "秒杀服务商品查询接口")
    @ApiImplicitParam(name = "token", value = "token验证信息", required = true, type = "String")
    public ResultVO selectProduct(@RequestHeader(required = true) String token) {
        Jws<Claims> jws = JWTUtil.Decrypt(token);
        String adminId = jws.getBody().getId();
        String issuer = jws.getBody().getIssuer();
        if ("admin".equals(issuer)) {
            List<Product> products = spikeService.listProductByAdminId(adminId);
            if (products != null) {
                return new ResultVO(0,"success",products);
            } else {
                return new ResultVO(1,"fail",null);
            }
        } else {
            return new ResultVO(1,"没有权限，请联系管理员",null);
        }
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ApiOperation(value = "秒杀活动列表查询接口" , notes = "秒杀活动列表查询接口")
    @ApiImplicitParam(name = "token", value = "token验证信息", required = true, type = "String")
    public ResultVO listSpike(@RequestHeader(required = true) String token) {
        Jws<Claims> jws = JWTUtil.Decrypt(token);
        String adminId = jws.getBody().getId();
        String issuer = jws.getBody().getIssuer();
        if ("admin".equals(issuer)) {
            List<FlashPromotion> flashPromotions = spikeService.listFlashPromotion(adminId);
            if (flashPromotions != null) {
                return new ResultVO(0,"success",flashPromotions);
            } else {
                return new ResultVO(1,"fail",null);
            }
        } else {
            return new ResultVO(1,"没有权限，请联系管理员",null);
        }
    }

    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    @ApiOperation(value = "秒杀活动列表查询接口" , notes = "秒杀活动列表查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "秒杀活动id", required = true, type = "Integer"),
            @ApiImplicitParam(name = "token", value = "token验证信息", required = true, type = "String")
    })
    public ResultVO deleteSpike(@RequestParam Integer id,@RequestHeader(required = true) String token) {
        Jws<Claims> jws = JWTUtil.Decrypt(token);
        String adminId = jws.getBody().getId();
        String issuer = jws.getBody().getIssuer();
        if ("admin".equals(issuer)) {
            boolean b = spikeService.deleteFlashPromotion(id);
            if (b) {
                return new ResultVO(0,"success");
            } else {
                return new ResultVO(1,"fail");
            }
        } else {
            return new ResultVO(1,"没有权限，请联系管理员",null);
        }
    }

    @RequestMapping(value = "/allList",method = RequestMethod.GET)
    @ApiOperation(value = "秒杀活动列表查询接口" , notes = "秒杀活动列表查询接口")
    @ApiImplicitParam(name = "token", value = "token验证信息", required = true, type = "String")
    public ResultVO allListSpike(@RequestHeader(required = true) String token) {
        Jws<Claims> jws = JWTUtil.Decrypt(token);
        String memberId = jws.getBody().getId();
        String issuer = jws.getBody().getIssuer();
        if ("member".equals(issuer)) {
            List<Map> flashPromotion = spikeService.getFlashPromotion();
            if (flashPromotion != null) {
                return new ResultVO(0,"success",flashPromotion);
            } else {
                return new ResultVO(1,"fail",null);
            }
        } else {
            return new ResultVO(1,"没有权限，请联系管理员",null);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "前台用户秒杀录单接口", notes = "用户秒杀录单的接口")
    @ApiImplicitParam(name = "token", value = "token验证信息", required = true, type = "String")
    public ResultVO addOrder(@RequestParam("companyId") String companyId ,
                             @RequestParam("relationId") Integer relationId,
                             @RequestParam("addressId") String addressId,
                             @RequestParam("productPic") String productPic,
                             @RequestParam("productId") String productId,
                             @RequestParam("productName") String productName,
                             @RequestParam("money") String money,
                             @RequestParam("num") String num,
                             @RequestParam("title") String title,
                             @RequestHeader(required = true) String token) throws IOException, TimeoutException {
        // 验证token
        Jws<Claims> jws = JWTUtil.Decrypt(token);
        // 获取解析的token中的用户名、id等
        String memberId = jws.getBody().getId();
        String issuer = jws.getBody().getIssuer();
        if ("member".equals(issuer)) {
            String orderId = UUID.randomUUID().toString().replace("-", "");
            double m1 = Double.parseDouble(money);
            int i = Integer.parseInt(num);

            String lockKey = productId + "-LOCK";
            RLock lock = redissonClient.getLock(lockKey);
            lock.lock();
            try {
                int flashPromotionCount = spikeService.getFlashPromotionCount(relationId);
                if (flashPromotionCount > 1) {
                    boolean b = spikeService.addOrder(new Order(0,orderId,memberId,new Date(),"lisi",m1,m1,0,0,0,0,addressId,7,null,null,0,null,null,null,null,companyId),
                            new OrderItem(relationId,orderId,orderId,productId,productPic,productName,null,m1,i,null,null,null,title,0.00,null));
                    if (b) {
//                        String msg = "已下单!";
//                        Connection connection = ConnectionUtil.getConnection();
//                        Channel channel = connection.createChannel();
//                        channel.queueDeclare("queue1",false,false,false,null);
//                        channel.basicPublish("","queue1",null,msg.getBytes());
//                        System.out.println("发送：" + msg);
//                        channel.close();
//                        connection.close();
                        return new ResultVO(0, "下单成功");
                    } else {
                        return new ResultVO(1, "下单失败");
                    }
                } else {
                    return new ResultVO(1, "下单失败，库存不足");
                }
            } finally {
                lock.unlock();
            }
        }else {
            return new ResultVO(1, "权限校验未通过");
        }
    }
}
