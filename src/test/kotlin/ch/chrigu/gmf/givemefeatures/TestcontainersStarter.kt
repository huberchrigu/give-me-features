package ch.chrigu.gmf.givemefeatures

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports

object TestcontainersStarter {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = TestcontainersConfiguration()
        config.mongoDbContainer()
            .withReuse(true)
            .withCreateContainerCmdModifier { cmd ->
                cmd.withHostConfig(
                    HostConfig().withPortBindings(PortBinding(Ports.Binding.bindPort(27017), ExposedPort(27017)))
                )
                    .withName("mongo")
            }
            .start()
    }
}
