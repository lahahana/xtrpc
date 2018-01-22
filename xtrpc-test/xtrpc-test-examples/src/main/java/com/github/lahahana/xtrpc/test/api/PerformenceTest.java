package com.github.lahahana.xtrpc.test.api;

public class PerformenceTest {

//    @Test
//    public void testDirectRefClient() throws Exception {
//        try{
//            List<Long> costTimeList = Collections.synchronizedList(new ArrayList<Long>());
//            for (int i = 0; i < 1; i++) {
//                Future<Long> f = testExecutors.submit(new AddressTask(addressService));
//                costTimeList.add(f.get());
//            }
//
//            long averageCostTime = costTimeList.stream().reduce((x, y) -> x + y).get() / costTimeList.size();
//            costTimeList.stream().forEach((time) -> {
//                logger.info("{}", time);
//            });
//            logger.info("Total task:{}, Average cost time:{}", costTimeList.size(), averageCostTime);
//        } finally {
//            while (testExecutors.isTerminated()) {
//                testExecutors.shutdown();
//            }
//        }
//    }
}
