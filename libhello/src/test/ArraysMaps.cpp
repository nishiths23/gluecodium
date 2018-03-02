// -------------------------------------------------------------------------------------------------
//
// Copyright (C) 2018 HERE Global B.V. and/or its affiliated companies. All rights reserved.
//
// This software, including documentation, is protected by copyright controlled by
// HERE Global B.V. All rights are reserved. Copying, including reproducing, storing,
// adapting or translating, any or all of this material requires the prior written
// consent of HERE Global B.V. This material also contains confidential information,
// which may not be disclosed to others without prior written consent of HERE Global B.V.
//
// -------------------------------------------------------------------------------------------------

#include <cstdint>
#include <string>
#include <vector>
#include <sstream>
#include "test/ArraysMaps.h"

namespace test {

std::vector< ::test::ArraysMaps::FancyMap >
ArraysMaps::reverse_map_array( const std::vector< ::test::ArraysMaps::FancyMap >& input )
{
    return { input.rbegin( ), input.rend( ) };
}

}
