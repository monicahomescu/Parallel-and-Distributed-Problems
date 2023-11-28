namespace PDP_lab4
{
    public class Program
    {
        public static void Main()
        {
            var hosts = new[] { "relaxedtranscendentyoungmoon.neverssl.com", "relaxedtranscendentyoungmoon.neverssl.com" };

            CallbackImplementation.Run(hosts);
            //TaskImplementation.Run(hosts);
            //AsyncImplementation.Run(hosts);
        }
    }
}
